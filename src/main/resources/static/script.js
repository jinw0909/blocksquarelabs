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
   Active navigation state
============================== */

const navLinks = document.querySelectorAll(".nav a");
const brandLogo = document.querySelector(".brand");

let isNavScrolling = false;
let navScrollTimer = null;

function setActiveNav(hash) {
  navLinks.forEach((link) => {
    link.classList.toggle("active", link.getAttribute("href") === hash);
  });
}

function updateActiveNavByScroll() {
  // 네비 클릭으로 이동 중일 때는 스크롤 active 보정 중지
  if (isNavScrolling) return;

  const headerOffset = 140;
  const scrollPosition = window.scrollY + headerOffset;

  let currentHash = "#top";

  navLinks.forEach((link) => {
    const hash = link.getAttribute("href");
    const target = hash ? document.querySelector(hash) : null;

    if (target && target.offsetTop <= scrollPosition) {
      currentHash = hash;
    }
  });

  setActiveNav(currentHash);
}

navLinks.forEach((link) => {
  link.addEventListener("click", () => {
    const hash = link.getAttribute("href");
    const target = hash ? document.querySelector(hash) : null;

    if (!hash || !target) return;

    // 클릭한 메뉴 즉시 active
    setActiveNav(hash);

    // smooth scroll 이동 중에는 다른 메뉴 active 변경 방지
    isNavScrolling = true;

    clearTimeout(navScrollTimer);

    navScrollTimer = setTimeout(() => {
      isNavScrolling = false;
      updateActiveNavByScroll();
    }, 1500);
  });
});

brandLogo?.addEventListener("click", () => {
  setActiveNav("#top");

  isNavScrolling = true;
  clearTimeout(navScrollTimer);

  navScrollTimer = setTimeout(() => {
    isNavScrolling = false;
    updateActiveNavByScroll();
  }, 800);
});

window.addEventListener("scroll", updateActiveNavByScroll);
window.addEventListener("DOMContentLoaded", updateActiveNavByScroll);

updateActiveNavByScroll();


/* ==============================
   Contact form / success modal
============================== */

// const contactForm = document.getElementById("contactForm");
// const successModal = document.getElementById("successModal");
// const modalClose = document.getElementById("modalClose");
// const modalOk = document.getElementById("modalOk");
//
// if (contactForm) {
//   contactForm.addEventListener("submit", async function (e) {
//     e.preventDefault();
//
//     const data = {
//       name: contactForm.name.value,
//       email: contactForm.email.value,
//       type: contactForm.type.value,
//       inquiry: contactForm.inquiry.value,
//       website: contactForm.website.value,
//     };
//
//     try {
//       const response = await fetch("/api/inquiry", {
//         method: "POST",
//         headers: {
//           "Content-Type": "application/json",
//         },
//         body: JSON.stringify(data),
//       });
//
//       if (response.status === 200) {
//         successModal.classList.add("show");
//         contactForm.reset();
//       } else {
//         alert("문의 전송에 실패했습니다.");
//       }
//     } catch (error) {
//       console.error(error);
//       alert("네트워크 오류");
//     }
//   });
// }



// const contactForm = document.getElementById("contactForm");
// const successModal = document.getElementById("successModal");
// const modalClose = document.getElementById("modalClose");
// const modalOk = document.getElementById("modalOk");
//
// const modalTitle = document.getElementById("modalTitle");
// const modalMessage = document.getElementById("modalMessage");
// const modalSpinner = document.getElementById("modalSpinner");
//
// function openModal() {
//   successModal?.classList.add("show");
// }
//
// function closeModal() {
//   successModal?.classList.remove("show");
// }
//
// function setModalLoading() {
//   modalTitle.textContent = "문의 접수 중";
//   modalMessage.innerHTML = "문의를 접수하고 있습니다.<br>잠시만 기다려 주세요.";
//   modalSpinner.style.display = "block";
//   modalOk.style.display = "none";
//   modalClose.style.display = "none";
// }
//
// function setModalSuccess() {
//   modalTitle.textContent = "문의 접수 완료";
//   modalMessage.innerHTML =
//       "문의가 정상적으로 접수되었습니다.<br>" +
//       "입력하신 이메일로 접수 확인 메일을 발송했습니다.<br>" +
//       "메일함을 확인해 주세요.";
//   modalSpinner.style.display = "none";
//   modalOk.style.display = "inline-flex";
//   modalClose.style.display = "block";
// }
//
// function setModalFail(message = "문의 접수에 실패했습니다.<br>잠시 후 다시 시도해 주세요.") {
//   modalTitle.textContent = "문의 접수 실패";
//   modalMessage.innerHTML = message;
//   modalSpinner.style.display = "none";
//   modalOk.style.display = "inline-flex";
//   modalClose.style.display = "block";
// }
//
// if (contactForm) {
//   contactForm.addEventListener("submit", async function (e) {
//     e.preventDefault();
//
//     const data = {
//       name: contactForm.name.value,
//       email: contactForm.email.value,
//       type: contactForm.type.value,
//       inquiry: contactForm.inquiry.value,
//       website: contactForm.website.value,
//     };
//
//     setModalLoading();
//     openModal();
//
//     const controller = new AbortController();
//     const timeoutId = setTimeout(() => controller.abort(), 10000);
//
//     try {
//       const response = await fetch("/api/inquiry", {
//         method: "POST",
//         headers: {
//           "Content-Type": "application/json",
//         },
//         body: JSON.stringify(data),
//         signal: controller.signal,
//       });
//
//       clearTimeout(timeoutId);
//
//       if (response.ok) {
//         setModalSuccess();
//         contactForm.reset();
//       } else {
//         setModalFail();
//       }
//     } catch (error) {
//       clearTimeout(timeoutId);
//       console.error(error);
//
//       if (error.name === "AbortError") {
//         setModalFail(
//             "요청 시간이 초과되었습니다.<br>네트워크 상태를 확인한 후 다시 시도해 주세요."
//         );
//       } else {
//         setModalFail(
//             "네트워크 오류가 발생했습니다.<br>잠시 후 다시 시도해 주세요."
//         );
//       }
//     }
//   });
// }
//
// modalClose?.addEventListener("click", closeModal);
// modalOk?.addEventListener("click", closeModal);
//
// successModal?.addEventListener("click", function (e) {
//   if (e.target === successModal && modalSpinner.style.display !== "block") {
//     closeModal();
//   }
// });


const contactForm = document.getElementById("contactForm");
const successModal = document.getElementById("successModal");
const modalClose = document.getElementById("modalClose");
const modalOk = document.getElementById("modalOk");
const modalSpinner = document.getElementById("modalSpinner");
const modalStates = document.querySelectorAll("[data-modal-state]");
const submitButton = contactForm?.querySelector('button[type="submit"]');

let currentModalState = "loading";
let submitLockedAfterSuccess = false;

function applyCurrentLanguage() {
  const language = localStorage.getItem(LANGUAGE_STORAGE_KEY) || DEFAULT_LANGUAGE;
  setLanguage(language);
}

function setSubmitReady() {
  if (!submitButton) return;

  submitButton.disabled = false;
  submitButton.classList.remove("disabled");
  submitButton.setAttribute("data-i18n", "contact.form.submit");
  submitLockedAfterSuccess = false;

  applyCurrentLanguage();
}

function setSubmitLoading() {
  if (!submitButton) return;

  submitButton.disabled = true;
  submitButton.classList.add("disabled");
  submitButton.setAttribute("data-i18n", "contact.form.sending");

  applyCurrentLanguage();
}

function setSubmitCompleted() {
  if (!submitButton) return;

  submitButton.disabled = true;
  submitButton.classList.add("disabled");
  submitButton.setAttribute("data-i18n", "contact.form.submitted");
  submitLockedAfterSuccess = true;

  applyCurrentLanguage();
}

function openModal() {
  successModal?.classList.add("show");
}

function closeModal() {
  successModal?.classList.remove("show");
}

function setModalState(state) {
  currentModalState = state;

  modalStates.forEach((element) => {
    element.style.display = element.dataset.modalState === state ? "block" : "none";
  });

  const isLoading = state === "loading";

  if (modalSpinner) {
    modalSpinner.style.display = isLoading ? "block" : "none";
  }

  if (modalOk) {
    modalOk.style.display = isLoading ? "none" : "inline-flex";
  }

  if (modalClose) {
    modalClose.style.display = isLoading ? "none" : "block";
  }
}

if (contactForm) {
  contactForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const data = {
      name: contactForm.name.value,
      email: contactForm.email.value,
      type: contactForm.type.value,
      inquiry: contactForm.inquiry.value,
      website: contactForm.website.value,
    };

    setSubmitLoading();
    setModalState("loading");
    openModal();

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    try {
      const response = await fetch("/api/inquiry", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
        signal: controller.signal,
      });

      clearTimeout(timeoutId);

      if (response.ok) {
        setModalState("success");
        setSubmitCompleted();
        // contactForm.reset();
      } else {
        setModalState("fail");
        setSubmitReady();
      }
    } catch (error) {
      clearTimeout(timeoutId);
      console.error(error);

      if (error.name === "AbortError") {
        setModalState("timeout");
      } else {
        setModalState("network");
      }

      setSubmitReady();
    }
  });

  contactForm?.querySelectorAll("input, textarea").forEach((field) => {
    field.addEventListener("input", () => {
      if (submitLockedAfterSuccess) {
        setSubmitReady();
      }
    });
  });
}

modalClose?.addEventListener("click", closeModal);
modalOk?.addEventListener("click", closeModal);

successModal?.addEventListener("click", function (e) {
  if (e.target === successModal && currentModalState !== "loading") {
    closeModal();
  }
});


// Close the contact success modal.
// function closeModal() {
//   if (successModal) {
//     successModal.classList.remove("show");
//   }
// }
//
// if (modalClose) {
//   modalClose.addEventListener("click", closeModal);
// }
//
// if (modalOk) {
//   modalOk.addEventListener("click", closeModal);
// }
//
// if (successModal) {
//   successModal.addEventListener("click", function (e) {
//     if (e.target === successModal) {
//       closeModal();
//     }
//   });
// }

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
