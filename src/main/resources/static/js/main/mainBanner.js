document.addEventListener('DOMContentLoaded',()=>{
    const slider = document.querySelector(".js-main-banner-slider");
    const slides = slider.querySelectorAll(".banner-slide");
    const prevBtn = slider.querySelector(".js-main-banner-prev");
    const nextBtn = slider.querySelector(".js-main-banner-next");
    const dots = slider.querySelectorAll(".js-banner-dot");

    //현재 인덱스
    let currentIndex = 0;
    // 슬라이드 전체 개수
    const totalSlides = slides.length;
    //시간 간격
    const intervalTime = Number(slider.dataset.interval) || 10000;
    let autoSlideTimer;

    function showSlide(index){
        if(index<0){
            index=totalSlides-1;
        }else if(index>=totalSlides){
            index=0;
        }
        currentIndex=index;

        //해당 슬라이드로 가면 해당 배너 보이게
        slides.forEach(function(slide,slideIndex){
            if(slideIndex===currentIndex){
                slide.classList.add("active");
            }else{
                slide.classList.remove("active");
            }
        });
        // 점 누르면 해당 배너로 이동
        dots.forEach(function (dot){
            const dotIndex = Number(dot.dataset.index);
            if(dotIndex===currentIndex){
                dot.classList.add("active");
            }else{
                dot.classList.remove("active");
            }
        });
    }
    function startAutoSlide() {
            autoSlideTimer = setInterval(function(){
                showSlide(currentIndex+1);
            },intervalTime);
        }
        function restAutoSlide() {
            clearInterval(autoSlideTimer);
            startAutoSlide;
        }
        // 이전 버튼
        if(prevBtn){
            prevBtn.addEventListener("click",function(){
                showSlide(currentIndex-1);
            });
        }
        // 다음 버튼
        if(nextBtn){
            nextBtn.addEventListener("click",function(){
                showSlide(currentIndex+1);
            });
        }
        // 점 눌러서 이동 가능
        dots.forEach(function(dot){
            dot.addEventListener("click",function(){
                const index = Number(dot.dataset.index);
                showSlide(index);
            });
        });

    
    showSlide(0);
    startAutoSlide();

});
