document.addEventListener('DOMContentLoaded', function() {
    const emailInput = document.getElementById('email');
    const phoneInput = document.getElementById('phone');
    const authRadios = document.querySelectorAll('input[name="authMethod"]');
    const sendAuthBtn = document.getElementById('sendAuthBtn');
    const verificationRow = document.getElementById('verificationRow');
    const verificationCode = document.getElementById('verificationCode');
    const timerDisplay = document.getElementById('timerDisplay');
    const usernameInput = document.getElementById('username');
    const checkDupBtn = document.querySelector('.input-with-btn .inline-action-btn');

    // 최초 로드 시 아이디가 입력되지 않으면 중복 확인 버튼 비활성화
    if (usernameInput && checkDupBtn) {
        checkDupBtn.disabled = !usernameInput.value.trim();

        // 아이디 입력 여부에 따른 중복 확인 버튼 활성화 제어
        usernameInput.addEventListener('input', function() {
            // 입력된 값이 영어와 숫자 조합인지 확인
            const hasInvalidChar = /[^a-zA-Z0-9]/.test(usernameInput.value);

            if (hasInvalidChar) {
                // 영어와 숫자 조합이 아니라면 실시간으로 입력창에서 삭제
                usernameInput.value = usernameInput.value.replace(/[^a-zA-Z0-9]/g, '');
                // 잘못된 문자를 입력했다는 에러 메시지 출력
                if (usernameError) {
                    usernameError.textContent = '아이디는 영문자와 숫자만 입력 가능합니다.';
                }
            } else {
                // 다시 아이디를 입력하기 시작하면 에러 메시지 초기화
                if (usernameError) {
                    usernameError.textContent = '';
                }
            }
            checkDupBtn.disabled = !usernameInput.value.trim();
        });

        // 중복 버튼 클릭 이벤트
        checkDupBtn.addEventListener('click', function() {
            const username = usernameInput.value.trim();
            // 아이디가 입력된 값이 없다면 중복을 확인하지 않음
            // 브라우저 개발자 모드로 버튼을 활성화시키는 우회 조작을 방지
            if (!username) return;

            // 서버에게 GET방식으로 요청 (파라미터는 PathVariables 방식으로 넘김)
            fetch(`/chkid-dup/${username}`, {
                method: 'GET'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('네트워크에 문제가 발생했습니다.');
                }
                return response.json(); // 서버에서 boolean 값을 리턴 (isDuplicate)
            })
            .then(isDuplicate => {
                if (isDuplicate) {
                    // 중복된 아이디면 에러 메시지 출력
                    if (usernameError) {
                        usernameError.textContent = '이미 사용중인 아이디입니다.';
                    }
                } else {
                    // 중복된 아이디가 아니면 아이디 입력창 및 버튼 비활성화 (readOnly)
                    checkDupBtn.disabled = true;
                    usernameInput.readOnly = true;
                    if (usernameError) {
                        usernameError.style.color = '#2DB400'
                        usernameError.textContent = '사용 가능한 아이디입니다.';
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });
    }
    
    // let timerInterval = null;

    // function checkAuthValidity() {
    //     if (authRadios[0].disabled) return;
        
    //     let selectedMethod = document.querySelector('input[name="authMethod"]:checked');
        
    //     if (!selectedMethod) {
    //         sendAuthBtn.disabled = true;
    //         return;
    //     }

    //     if (selectedMethod.value === 'email') {
    //         if (emailInput.value.trim() !== '' && emailInput.checkValidity()) {
    //             sendAuthBtn.disabled = false;
    //         } else {
    //             sendAuthBtn.disabled = true;
    //         }
    //     } else if (selectedMethod.value === 'phone') {
    //         if (phoneInput.value.length === 13 && phoneInput.checkValidity()) {
    //             sendAuthBtn.disabled = false;
    //         } else {
    //             sendAuthBtn.disabled = true;
    //         }
    //     }
    // }

    // emailInput.addEventListener('input', checkAuthValidity);
    // phoneInput.addEventListener('input', checkAuthValidity);
    // authRadios.forEach(radio => radio.addEventListener('change', checkAuthValidity));

    // sendAuthBtn.addEventListener('click', function() {
    //     if (!sendAuthBtn.disabled) {
    //         let selectedMethod = document.querySelector('input[name="authMethod"]:checked');
            
    //         authRadios.forEach(radio => radio.disabled = true);
            
    //         if (selectedMethod && selectedMethod.value === 'email') {
    //             emailInput.readOnly = true;
    //         } else if (selectedMethod && selectedMethod.value === 'phone') {
    //             phoneInput.readOnly = true;
    //         }
            

    //         verificationRow.style.display = 'flex';
    //         verificationCode.setAttribute('required', 'required');
            
    //         if (timerInterval) clearInterval(timerInterval);
            
    //         let timeLeft = 180;
            
    //         function updateTimer() {
    //             if (timeLeft <= 0) {
    //                 clearInterval(timerInterval);
    //                 timerDisplay.textContent = "인증번호 만료";
    //                 return;
    //             }
    //             timeLeft--;
    //             let minutes = Math.floor(timeLeft / 60);
    //             let seconds = timeLeft % 60;
    //             let secondsStr = seconds < 10 ? '0' + seconds : seconds;
    //             timerDisplay.textContent = `남은 시간 : ${minutes}분 ${secondsStr}초`;
    //         }
            
    //         timerInterval = setInterval(updateTimer, 1000);
    //     }
    // });
});