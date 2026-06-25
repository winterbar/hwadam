const like = document.getElementById('like-icon');

like.addEventListener('click', function () {

    if (like.classList.contains('on-like')) {
        like.textContent = '♡';
        like.classList.remove('on-like');
    }
    else {
        like.textContent = '♥';
        like.classList.add('on-like');
    }
})