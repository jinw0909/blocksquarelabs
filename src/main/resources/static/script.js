const DEFAULT_LANGUAGE = "ko";
const LANGUAGE_STORAGE_KEY = "blocksquare-language";

let translations = {};

function getValue(path, source) {
  return path.split(".").reduce((current, key) => {
    if (Array.isArray(current)) {
      return current[Number(key)];
    }
    return current ? current[key] : undefined;
  }, source);
}

function setLanguage(language) {
  const dictionary = translations[language] || translations[DEFAULT_LANGUAGE];
  if (!dictionary) return;

  document.documentElement.lang = language === "ko" ? "ko" : "en";

  document.querySelectorAll("[data-i18n]").forEach((element) => {
    const value = getValue(element.dataset.i18n, dictionary);
    if (value !== undefined) {
      element.textContent = value;
    }
  });

  document.querySelectorAll("[data-i18n-html]").forEach((element) => {
    const value = getValue(element.dataset.i18nHtml, dictionary);
    if (value !== undefined) {
      element.innerHTML = value;
    }
  });

  document.querySelectorAll("[data-i18n-attr]").forEach((element) => {
    element.dataset.i18nAttr.split(",").forEach((pair) => {
      const [attribute, path] = pair.split(":");
      const value = getValue(path, dictionary);
      if (attribute && value !== undefined) {
        element.setAttribute(attribute, value);
      }
    });
  });

  const title = getValue("meta.title", dictionary);
  if (title) {
    document.title = title;
  }

  const select = document.querySelector("#languageSelect");
  if (select) {
    select.value = language;
  }

  localStorage.setItem(LANGUAGE_STORAGE_KEY, language);
}

async function initLanguage() {
  const response = await fetch("translations.json");
  translations = await response.json();

  const savedLanguage = localStorage.getItem(LANGUAGE_STORAGE_KEY);
  const initialLanguage = translations[savedLanguage] ? savedLanguage : DEFAULT_LANGUAGE;
  setLanguage(initialLanguage);

  document.querySelector("#languageSelect")?.addEventListener("change", (event) => {
    setLanguage(event.target.value);
  });
}

initLanguage().catch((error) => {
  console.error("Failed to load translations.", error);
});
