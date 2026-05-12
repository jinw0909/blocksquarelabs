/*
 * Blocksquare Labs landing page script
 * - Loads i18n text from translations.json
 * - Controls the custom language selector
 * - Submits the contact form
 * - Controls the project image slider
 *
 * NOTE: This file only reorganizes comments/spacing. Runtime behavior is kept the same.
 */

/* ==============================
   Global constants / state
============================== */

const DEFAULT_LANGUAGE = "ko";
const LANGUAGE_STORAGE_KEY = "blocksquare-language";

const LANGUAGE_LABELS = {
  ko: "KR",
  en: "EN",
  jp: "JP",
  cn: "CN",
};

const HTML_LANGS = {
  ko: "ko",
  en: "en",
  jp: "ja",
  cn: "zh-CN",
};


let translations = {};

/* ==============================
   Translation helpers
============================== */

/**
 * Reads a nested value from an object using dot notation.
 * Example: getValue("about.strengths.0.title", dictionary)
 */
function getValue(path, source) {
  return path.split(".").reduce((current, key) => {
    if (Array.isArray(current)) {
      return current[Number(key)];
    }

    return current ? current[key] : undefined;
  }, source);
}

/**
 * Applies the selected language to all elements using data-i18n attributes.
 *
 * Supported attributes:
 * - data-i18n: sets textContent
 * - data-i18n-html: sets innerHTML
 * - data-i18n-attr: sets one or more attributes, e.g. "placeholder:contact.form.namePlaceholder"
 */
// function setLanguage(language) {
//   const dictionary = translations[language] || translations[DEFAULT_LANGUAGE];
//   if (!dictionary) return;
//
//   // Keep the root lang attribute aligned with the current language.
//   document.documentElement.lang = HTML_LANGS[language] || HTML_LANGS[DEFAULT_LANGUAGE];
//
//   // Plain-text translations.
//   document.querySelectorAll("[data-i18n]").forEach((element) => {
//     const value = getValue(element.dataset.i18n, dictionary);
//     if (value !== undefined) {
//       element.textContent = value;
//     }
//   });
//
//   // HTML translations. Used only where line breaks or inline markup are expected.
//   document.querySelectorAll("[data-i18n-html]").forEach((element) => {
//     const value = getValue(element.dataset.i18nHtml, dictionary);
//     if (value !== undefined) {
//       element.innerHTML = value;
//     }
//   });
//
//   // Attribute translations such as placeholder, aria-label, alt, and meta content.
//   document.querySelectorAll("[data-i18n-attr]").forEach((element) => {
//     element.dataset.i18nAttr.split(",").forEach((pair) => {
//       const [attribute, path] = pair.split(":");
//       const value = getValue(path, dictionary);
//
//       if (attribute && value !== undefined) {
//         element.setAttribute(attribute, value);
//       }
//     });
//   });
//
//   // Browser tab title.
//   const title = getValue("meta.title", dictionary);
//   if (title) {
//     document.title = title;
//   }
//
//   // Custom language menu label.
//   const currentLanguageText = document.querySelector("#currentLanguageText");
//   if (currentLanguageText) {
//     currentLanguageText.textContent = language === "ko" ? "KR" : "EN";
//   }
//
//   // Highlight the currently selected language button.
//   document.querySelectorAll(".lang-option").forEach((button) => {
//     button.classList.toggle("active", button.dataset.language === language);
//   });
//
//   // Persist the user's language choice.
//   localStorage.setItem(LANGUAGE_STORAGE_KEY, language);
// }
function setLanguage(language) {
  const dictionary = translations[language] || translations[DEFAULT_LANGUAGE];
  if (!dictionary) return;

  // Keep the root lang attribute aligned with the current language.
  document.documentElement.lang = HTML_LANGS[language] || HTML_LANGS[DEFAULT_LANGUAGE];

  // Plain-text translations.
  document.querySelectorAll("[data-i18n]").forEach((element) => {
    const value = getValue(element.dataset.i18n, dictionary);
    if (value !== undefined) {
      element.textContent = value;
    }
  });

  // HTML translations. Used only where line breaks or inline markup are expected.
  document.querySelectorAll("[data-i18n-html]").forEach((element) => {
    const value = getValue(element.dataset.i18nHtml, dictionary);
    if (value !== undefined) {
      element.innerHTML = value;
    }
  });

  // Attribute translations such as placeholder, aria-label, alt, and meta content.
  document.querySelectorAll("[data-i18n-attr]").forEach((element) => {
    element.dataset.i18nAttr.split(",").forEach((pair) => {
      const [attribute, path] = pair.split(":");
      const value = getValue(path, dictionary);

      if (attribute && value !== undefined) {
        element.setAttribute(attribute, value);
      }
    });
  });

  // Browser tab title.
  const title = getValue("meta.title", dictionary);
  if (title) {
    document.title = title;
  }

  // Custom language menu label.
  const currentLanguageText = document.querySelector("#currentLanguageText");
  if (currentLanguageText) {
    currentLanguageText.textContent = LANGUAGE_LABELS[language] || LANGUAGE_LABELS[DEFAULT_LANGUAGE];
  }

  // Highlight the currently selected language button.
  document.querySelectorAll(".lang-option").forEach((button) => {
    button.classList.toggle("active", button.dataset.language === language);
  });

  // Persist the user's language choice.
  localStorage.setItem(LANGUAGE_STORAGE_KEY, language);
}
/**
 * Loads translations and wires up the language dropdown.
 */
async function initLanguage() {
  const response = await fetch("translations.json");
  translations = await response.json();

  const savedLanguage = localStorage.getItem(LANGUAGE_STORAGE_KEY);
  const initialLanguage = translations[savedLanguage] ? savedLanguage : DEFAULT_LANGUAGE;
  setLanguage(initialLanguage);

  const languageTrigger = document.querySelector("#languageTrigger");
  const languageModal = document.querySelector("#languageModal");
  const languageOptions = document.querySelectorAll(".lang-option");

  function closeLanguageModal() {
    languageModal?.classList.remove("show");
    languageModal?.setAttribute("aria-hidden", "true");
  }

  function openLanguageModal() {
    languageModal?.classList.add("show");
    languageModal?.setAttribute("aria-hidden", "false");
  }

  // Toggle the language menu without letting the click bubble to document.
  languageTrigger?.addEventListener("click", (event) => {
    event.stopPropagation();

    if (languageModal?.classList.contains("show")) {
      closeLanguageModal();
    } else {
      openLanguageModal();
    }
  });

  // Change language from the custom language menu.
  languageOptions.forEach((button) => {
    button.addEventListener("click", (event) => {
      event.stopPropagation();

      const language = button.dataset.language;
      if (!language) return;

      setLanguage(language);
      closeLanguageModal();
    });
  });

  // Close the language menu when clicking outside of it.
  document.addEventListener("click", () => {
    closeLanguageModal();
  });

  // Prevent clicks inside the modal from closing it through the document handler.
  languageModal?.addEventListener("click", (event) => {
    event.stopPropagation();
  });
}

initLanguage().catch((error) => {
  console.error("Failed to load translations.", error);
});

/* ==============================
   Contact form / success modal
============================== */

const contactForm = document.getElementById("contactForm");
const successModal = document.getElementById("successModal");
const modalClose = document.getElementById("modalClose");
const modalOk = document.getElementById("modalOk");

if (contactForm) {
  contactForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const data = {
      name: contactForm.name.value,
      email: contactForm.email.value,
      type: contactForm.type.value,
      inquiry: contactForm.inquiry.value,
    };

    try {
      const response = await fetch("/api/inquiry", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });

      if (response.status === 200) {
        successModal.classList.add("show");
        contactForm.reset();
      } else {
        alert("문의 전송에 실패했습니다.");
      }
    } catch (error) {
      console.error(error);
      alert("네트워크 오류");
    }
  });
}

// Close the contact success modal.
function closeModal() {
  if (successModal) {
    successModal.classList.remove("show");
  }
}

if (modalClose) {
  modalClose.addEventListener("click", closeModal);
}

if (modalOk) {
  modalOk.addEventListener("click", closeModal);
}

if (successModal) {
  successModal.addEventListener("click", function (e) {
    if (e.target === successModal) {
      closeModal();
    }
  });
}

/* ==============================
   Project showcase slider
============================== */

const projectSlides = document.querySelectorAll(".dashboard-slide");
const projectPrevButton = document.querySelector(".project-prev");
const projectNextButton = document.querySelector(".project-next");

let currentProjectSlide = 0;

function showProjectSlide(index) {
  if (!projectSlides.length) return;

  projectSlides[currentProjectSlide].classList.remove("active");

  currentProjectSlide = (index + projectSlides.length) % projectSlides.length;

  projectSlides[currentProjectSlide].classList.add("active");
}

if (projectPrevButton && projectNextButton && projectSlides.length) {
  projectPrevButton.addEventListener("click", () => {
    showProjectSlide(currentProjectSlide - 1);
  });

  projectNextButton.addEventListener("click", () => {
    showProjectSlide(currentProjectSlide + 1);
  });
}
