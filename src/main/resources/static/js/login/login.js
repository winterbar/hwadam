// 비밀번호 표시 버튼
document.addEventListener('DOMContentLoaded', function() {
    const togglePassword = document.getElementById('toggle-password');
    const password = document.getElementById('password');
    const eyeIcon = document.getElementById('eyeIcon')

    togglePassword.addEventListener('click', function() {
        const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
        password.setAttribute('type', type);

        if (type === 'password') {
            eyeIcon.src = '/images/icons/crossed-eye.png';
        } else {
            eyeIcon.src = '/images/icons/eye.png';
        }
    })
});