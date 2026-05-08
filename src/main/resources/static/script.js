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

// function setLanguage(language) {
//   const dictionary = translations[language] || translations[DEFAULT_LANGUAGE];
//   if (!dictionary) return;
//
//   document.documentElement.lang = language === "ko" ? "ko" : "en";
//
//   document.querySelectorAll("[data-i18n]").forEach((element) => {
//     const value = getValue(element.dataset.i18n, dictionary);
//     if (value !== undefined) {
//       element.textContent = value;
//     }
//   });
//
//   document.querySelectorAll("[data-i18n-html]").forEach((element) => {
//     const value = getValue(element.dataset.i18nHtml, dictionary);
//     if (value !== undefined) {
//       element.innerHTML = value;
//     }
//   });
//
//   document.querySelectorAll("[data-i18n-attr]").forEach((element) => {
//     element.dataset.i18nAttr.split(",").forEach((pair) => {
//       const [attribute, path] = pair.split(":");
//       const value = getValue(path, dictionary);
//       if (attribute && value !== undefined) {
//         element.setAttribute(attribute, value);
//       }
//     });
//   });
//
//   const title = getValue("meta.title", dictionary);
//   if (title) {
//     document.title = title;
//   }
//
//
//   // 언어 선택
//   // const select = document.querySelector("#languageSelect");
//   // if (select) {
//   //   select.value = language;
//   // }
//
//   const currentLanguageText = document.querySelector("#currentLanguageText");
//   if (currentLanguageText) {
//     currentLanguageText.textContent = language === "ko" ? "KR" : "EN";
//   }
//
//   document.querySelectorAll(".lang-option").forEach((button) => {
//     button.classList.toggle("active", button.dataset.language === language);
//   });
//
//   localStorage.setItem(LANGUAGE_STORAGE_KEY, language);
// }



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

  const currentLanguageText = document.querySelector("#currentLanguageText");
  if (currentLanguageText) {
    currentLanguageText.textContent = language === "ko" ? "KR" : "EN";
  }

  document.querySelectorAll(".lang-option").forEach((button) => {
    button.classList.toggle("active", button.dataset.language === language);
  });

  localStorage.setItem(LANGUAGE_STORAGE_KEY, language);
}

// async function initLanguage() {
//   const response = await fetch("translations.json");
//   translations = await response.json();
//
//   const savedLanguage = localStorage.getItem(LANGUAGE_STORAGE_KEY);
//   const initialLanguage = translations[savedLanguage] ? savedLanguage : DEFAULT_LANGUAGE;
//   setLanguage(initialLanguage);
//
//   // document.querySelector("#languageSelect")?.addEventListener("change", (event) => {
//   //   setLanguage(event.target.value);
//   // });
//   const languageTrigger = document.querySelector("#languageTrigger");
//   const languageModal = document.querySelector("#languageModal");
//   const languageOptions = document.querySelectorAll(".lang-option");
//
//   function closeLanguageModal() {
//     languageModal?.classList.remove("show");
//     languageModal?.setAttribute("aria-hidden", "true");
//   }
//
//   function openLanguageModal() {
//     languageModal?.classList.add("show");
//     languageModal?.setAttribute("aria-hidden", "false");
//   }
//
//   languageTrigger?.addEventListener("click", (event) => {
//     event.stopPropagation();
//
//     if (languageModal?.classList.contains("show")) {
//       closeLanguageModal();
//     } else {
//       openLanguageModal();
//     }
//   });
//
//   languageOptions.forEach((button) => {
//     button.addEventListener("click", (event) => {
//       event.stopPropagation();
//
//       const language = button.dataset.language;
//       if (!language) return;
//
//       setLanguage(language);
//       closeLanguageModal();
//     });
//   });
//
//   document.addEventListener("click", () => {
//     closeLanguageModal();
//   });
//
//   languageModal?.addEventListener("click", (event) => {
//     event.stopPropagation();
//   });
// }
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

  languageTrigger?.addEventListener("click", (event) => {
    event.stopPropagation();

    if (languageModal?.classList.contains("show")) {
      closeLanguageModal();
    } else {
      openLanguageModal();
    }
  });

  languageOptions.forEach((button) => {
    button.addEventListener("click", (event) => {
      event.stopPropagation();

      const language = button.dataset.language;
      if (!language) return;

      setLanguage(language);
      closeLanguageModal();
    });
  });

  document.addEventListener("click", () => {
    closeLanguageModal();
  });

  languageModal?.addEventListener("click", (event) => {
    event.stopPropagation();
  });
}
initLanguage().catch((error) => {
  console.error("Failed to load translations.", error);
});

// 문의 API 전송 + 팝업


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
      const response = await fetch("https://blocksquarelabs.com/api/inquiry", {
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

// 팝업 닫기
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

// 슬라이드
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