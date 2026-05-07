const sharp = require("sharp");

const source = "/Users/hongchang-gug/Downloads/KR.png";
const crops = [
  ["kr-hero-bg.png", { left: 0, top: 70, width: 1920, height: 980 }],
  ["kr-services-bg.png", { left: 760, top: 4300, width: 1160, height: 900 }],
  ["kr-projects-bg.png", { left: 0, top: 5520, width: 1920, height: 1240 }],
  ["kr-project-dashboard.png", { left: 360, top: 6340, width: 1200, height: 760 }],
  ["kr-project-mobile.png", { left: 320, top: 7040, width: 1240, height: 650 }],
  ["kr-contact-map.png", { left: 875, top: 7960, width: 690, height: 310 }],
];

(async () => {
  for (const [name, region] of crops) {
    await sharp(source).extract(region).png().toFile(`assets/${name}`);
  }
})();
