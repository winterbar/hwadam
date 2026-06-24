document.addEventListener('DOMContentLoaded', function() {
    const usernameInput = document.getElementById('username');
    const validationMsg = document.getElementById('id-validation-msg');
    
    // 아이디에 영문자, 숫자만 들어오는지 실시간으로 감지
    usernameInput.addEventListener('input', function () {
        const value = this.value;
        const regex = /^[a-zA-Z0-9]*$/;

        if (value.length > 0 && !regex.test(value)) {
            validationMsg.style.display = 'block';
            this.value = value.replace(/[^a-zA-Z0-9]/g, '');
        } else {
            validationMsg.style.display = 'none';
        }
    });
});