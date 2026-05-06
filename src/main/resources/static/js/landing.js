(function () {
    "use strict";

    const header = document.getElementById("siteHeader");
    const progress = document.getElementById("scrollProgress");
    const menuButton = document.getElementById("mobileMenuBtn");
    const topNav = document.getElementById("topNav");

    function closeMenu() {
        if (!menuButton || !topNav) return;

        menuButton.classList.remove("is-open");
        topNav.classList.remove("is-open");
        menuButton.setAttribute("aria-expanded", "false");
        document.body.classList.remove("menu-open");
    }

    if (menuButton && topNav) {
        menuButton.addEventListener("click", () => {
            const isOpen = !topNav.classList.contains("is-open");

            menuButton.classList.toggle("is-open", isOpen);
            topNav.classList.toggle("is-open", isOpen);
            menuButton.setAttribute("aria-expanded", String(isOpen));
            document.body.classList.toggle("menu-open", isOpen);
        });

        topNav.querySelectorAll("a").forEach(link => {
            link.addEventListener("click", closeMenu);
        });
    }

    document.querySelectorAll("[data-go]").forEach(link => {
        link.addEventListener("click", event => {
            const href = link.getAttribute("href");

            if (!href) return;

            event.preventDefault();
            window.location.assign(href);
        });
    });

    function updateChrome(scrollY, progressValue) {
        if (header) {
            header.classList.toggle("is-compact", scrollY > 40);
        }

        if (progress) {
            progress.style.width = `${Math.max(0, Math.min(1, progressValue)) * 100}%`;
        }
    }

    window.addEventListener("scroll", () => {
        const max = document.documentElement.scrollHeight - window.innerHeight;
        const p = max > 0 ? window.scrollY / max : 0;

        updateChrome(window.scrollY, p);
    }, { passive: true });

    function setActive(index) {
        const blocks = document.querySelectorAll(".copy-block, .role-panel");
        const dots = document.querySelectorAll(".rail-dot");

        blocks.forEach(block => {
            const isActive = Number(block.dataset.step) === index;

            block.classList.toggle("is-active", isActive);

            if (block.classList.contains("role-panel")) {
                block.style.pointerEvents = isActive ? "auto" : "none";
            } else {
                block.style.pointerEvents = "none";
            }
        });

        dots.forEach(dot => {
            dot.classList.toggle("is-active", Number(dot.dataset.dot) === index);
        });
    }

    function initGsap() {
        if (!window.gsap || !window.ScrollTrigger) {
            document.body.classList.add("no-gsap");
            setActive(0);
            return;
        }

        gsap.registerPlugin(ScrollTrigger);

        const hero = document.querySelector(".scene-hero");
        const entrance = document.querySelector(".scene-entrance");
        const reception = document.querySelector(".scene-reception");
        const office = document.querySelector(".scene-office");

        const finalBackdrop = document.getElementById("finalBackdrop");
        const finalCaption = document.getElementById("finalCaption");

        const copyHero = document.querySelector(".copy-hero");
        const copyEntrance = document.querySelector(".copy-entrance");
        const patientPanel = document.querySelector(".patient-panel");
        const doctorPanel = document.querySelector(".doctor-panel");
        const quickPanel = document.querySelector(".quick-panel");

        gsap.set([hero, entrance, reception, office], {
            force3D: true,
            transformOrigin: "center center"
        });

        gsap.set([entrance, reception, office], {
            opacity: 0,
            scale: 1.06
        });

        gsap.set(hero, {
            opacity: 1,
            scale: 1.06
        });

        gsap.set(finalBackdrop, {
            opacity: 0,
            force3D: true
        });

        gsap.set(finalCaption, {
            autoAlpha: 0,
            y: 10,
            force3D: true
        });

        gsap.set([copyHero, copyEntrance, patientPanel, doctorPanel], {
            autoAlpha: 0,
            y: 22,
            force3D: true
        });

        gsap.set(quickPanel, {
            autoAlpha: 0,
            xPercent: -50,
            yPercent: -50,
            y: 24,
            force3D: true
        });

        gsap.set(copyHero, {
            autoAlpha: 1,
            y: 0
        });

        setActive(0);

        const TOTAL_SCROLL = 6900;

        const tl = gsap.timeline({
            defaults: {
                ease: "none"
            },
            scrollTrigger: {
                trigger: ".cinematic-experience",
                start: "top top",
                end: `+=${TOTAL_SCROLL}`,
                scrub: 1.15,
                pin: true,
                anticipatePin: 1,
                invalidateOnRefresh: true,
                onUpdate: self => {
                    const p = self.progress;

                    updateChrome(self.scroll(), p);

                    if (p < 0.18) {
                        setActive(0);
                    } else if (p < 0.39) {
                        setActive(1);
                    } else if (p < 0.63) {
                        setActive(2);
                    } else if (p < 0.82) {
                        setActive(3);
                    } else {
                        setActive(4);
                    }
                }
            }
        });

        tl
            .to(hero, {
                scale: 1.18,
                xPercent: -2,
                yPercent: -1.5,
                duration: 1.1
            }, 0)

            .to(copyHero, {
                y: -18,
                autoAlpha: 0,
                duration: 0.28,
                ease: "power2.in"
            }, 0.78)

            .to(hero, {
                opacity: 0,
                duration: 0.52,
                ease: "power1.inOut"
            }, 0.98)

            .to(entrance, {
                opacity: 1,
                scale: 1.02,
                duration: 0.65,
                ease: "power1.inOut"
            }, 0.88)

            .fromTo(copyEntrance,
                {
                    autoAlpha: 0,
                    y: 24
                },
                {
                    autoAlpha: 1,
                    y: 0,
                    duration: 0.36,
                    ease: "power2.out"
                },
                1.10
            )

            .to(entrance, {
                scale: 1.14,
                xPercent: -1.2,
                yPercent: -0.8,
                duration: 1.0
            }, 1.22)

            .to(copyEntrance, {
                y: -18,
                autoAlpha: 0,
                duration: 0.28,
                ease: "power2.in"
            }, 1.98)

            .to(entrance, {
                opacity: 0,
                duration: 0.52,
                ease: "power1.inOut"
            }, 2.18)

            .to(reception, {
                opacity: 1,
                scale: 1.02,
                duration: 0.65,
                ease: "power1.inOut"
            }, 2.06)

            .fromTo(patientPanel,
                {
                    autoAlpha: 0,
                    y: 26
                },
                {
                    autoAlpha: 1,
                    y: 0,
                    duration: 0.40,
                    ease: "power2.out"
                },
                2.34
            )

            .to(reception, {
                scale: 1.11,
                xPercent: 1.0,
                yPercent: -0.7,
                duration: 1.10
            }, 2.52)

            .to(patientPanel, {
                y: -18,
                autoAlpha: 0,
                duration: 0.28,
                ease: "power2.in"
            }, 3.34)

            .to(reception, {
                opacity: 0,
                duration: 0.52,
                ease: "power1.inOut"
            }, 3.56)

            .to(office, {
                opacity: 1,
                scale: 1.02,
                duration: 0.65,
                ease: "power1.inOut"
            }, 3.44)

            .fromTo(doctorPanel,
                {
                    autoAlpha: 0,
                    y: 26
                },
                {
                    autoAlpha: 1,
                    y: 0,
                    duration: 0.42,
                    ease: "power2.out"
                },
                3.74
            )

            .to(office, {
                scale: 1.10,
                xPercent: -1.0,
                yPercent: -0.8,
                duration: 1.10
            }, 3.98)

            .to(doctorPanel, {
                y: -18,
                autoAlpha: 0,
                duration: 0.28,
                ease: "power2.in"
            }, 4.88)

            .to(finalBackdrop, {
                opacity: 1,
                duration: 0.55,
                ease: "power1.out"
            }, 4.96)

            .fromTo(quickPanel,
                {
                    autoAlpha: 0,
                    xPercent: -50,
                    yPercent: -50,
                    y: 24
                },
                {
                    autoAlpha: 1,
                    xPercent: -50,
                    yPercent: -50,
                    y: 0,
                    duration: 0.45,
                    ease: "power2.out"
                },
                5.12
            )

            .fromTo(finalCaption,
                {
                    autoAlpha: 0,
                    y: 10
                },
                {
                    autoAlpha: 1,
                    y: 0,
                    duration: 0.36,
                    ease: "power2.out"
                },
                5.30
            )

            .to(office, {
                scale: 1.105,
                xPercent: -1.2,
                yPercent: -0.8,
                duration: 1.25
            }, 5.12);

        ScrollTrigger.refresh();

        const jumpPoints = [0, 0.22, 0.49, 0.72, 0.89];

        document.querySelectorAll("[data-jump]").forEach(link => {
            link.addEventListener("click", event => {
                event.preventDefault();

                const index = parseInt(link.dataset.jump, 10);
                const st = tl.scrollTrigger;

                if (!st) return;

                const targetScroll = st.start + jumpPoints[index] * (st.end - st.start);

                window.scrollTo({
                    top: targetScroll,
                    behavior: "smooth"
                });
            });
        });

        document.querySelectorAll(".rail-dot").forEach(dot => {
            dot.addEventListener("click", () => {
                const index = parseInt(dot.dataset.dot, 10);
                const st = tl.scrollTrigger;

                if (!st) return;

                const targetScroll = st.start + jumpPoints[index] * (st.end - st.start);

                window.scrollTo({
                    top: targetScroll,
                    behavior: "smooth"
                });
            });
        });
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initGsap);
    } else {
        initGsap();
    }
})();