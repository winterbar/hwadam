let timerInterval;
let currentGeneratedCode = { id: "", pw: "" };
let isPhoneButtonFlipped = { id: false, pw: false }; // '인증번호 발급' 진행 토글 변수
let isPhoneVerifiedMock = { id: false, pw: false };  // 실제 전송 완료 검증 Mock 플래그

// URL 파라미터를 읽어 해당 탭을 클릭해주는 초기화 로직
window.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const type = urlParams.get('type');
    if (type === 'pw') {
        switchFindType('pw');
    } else {
        switchFindType('id');
    }
});

// 1. 대형 탭 전환
function switchFindType(type) {
    const tabs = document.querySelectorAll('.find-type-tab');
    const sections = document.querySelectorAll('.find-section');
    
    tabs.forEach(tab => tab.classList.remove('active'));
    sections.forEach(sec => sec.classList.remove('active'));

    if (type === 'id') {
        tabs[0].classList.add('active');
        document.getElementById('findIdSection').classList.add('active');
    } else {
        tabs[1].classList.add('active');
        document.getElementById('findPwSection').classList.add('active');
    }
    clearInterval(timerInterval);
    document.getElementById('idVerificationRow').style.display = 'none';
    document.getElementById('pwVerificationRow').style.display = 'none';
    document.getElementById('idResultContainer').style.display = 'none';
    
    resetRadioButtons('id');
    resetRadioButtons('pw');
    clearMessage('id');
    clearMessage('pw');

    // 탭 변경 시 입력창 데이터 및 readOnly 완벽 초기화
    const allInputs = ['idEmailInput', 'idPhoneInput', 'pwEmailInput', 'pwPhoneInput', 'pwUsernameInput', 'idVerifyInput', 'pwVerifyInput'];
    allInputs.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.value = '';
            el.readOnly = false;
        }
    });
}

// 라디오 버튼 원상 복구 및 플래그 리셋
function resetRadioButtons(section) {
    const radios = document.querySelectorAll(`input[name="${section}AuthType"]`);
    radios.forEach(r => r.disabled = false);
    const btn = document.getElementById(`${section}PhoneBtn`);
    if(btn) btn.innerText = "인증번호 발급";
    isPhoneButtonFlipped[section] = false;
    isPhoneVerifiedMock[section] = false;
}

// 2. 인증 수단 라디오 버튼 스위칭 (요청에 따라 값 초기화 코드 삭제)
function toggleInputType(section, type) {
    const emailRow = document.getElementById(`${section}EmailFormRow`);
    const phoneRow = document.getElementById(`${section}PhoneFormRow`);
    
    const emailVerifyWrapper = document.getElementById(`${section}VerifyWrapper`);
    const phoneVerifyWrapper = document.getElementById(`${section}PhoneVerifyWrapper`);

    if (type === 'email') {
        emailRow.style.display = 'flex';
        phoneRow.style.display = 'none';
        document.getElementById(`${section}EmailInput`).required = true;
        document.getElementById(`${section}PhoneInput`).required = false;
        
        emailVerifyWrapper.style.display = 'block';
        phoneVerifyWrapper.style.display = 'none';
    } else {
        emailRow.style.display = 'none';
        phoneRow.style.display = 'flex';
        document.getElementById(`${section}EmailInput`).required = false;
        document.getElementById(`${section}PhoneInput`).required = true;
        
        emailVerifyWrapper.style.display = 'none';
        phoneVerifyWrapper.style.display = 'block';
    }

    document.getElementById(`${section}VerificationRow`).style.display = 'none';
    document.getElementById('idResultContainer').style.display = 'none';
    clearMessage(section);
    validateInput(section);
    resetRadioButtons(section);
}

// 3. 실시간 유효성 체크 및 버튼 제어
function validateInput(section) {
    const isIdSection = (section === 'id');
    const authType = document.querySelector(`input[name="${section}AuthType"]:checked`).value;
    
    let isUsernameValid = true;
    if (!isIdSection) {
        const usernameVal = document.getElementById('pwUsernameInput').value.trim();
        isUsernameValid = usernameVal.length > 0;
    }

    if (authType === 'email') {
        const emailInput = document.getElementById(`${section}EmailInput`);
        const btn = document.getElementById(`${section}EmailBtn`);
        const isEmailValid = emailInput.checkValidity() && emailInput.value.trim().length > 0;
        btn.disabled = !(isEmailValid && isUsernameValid);
    } else {
        const phone = document.getElementById(`${section}PhoneInput`).value;
        const btn = document.getElementById(`${section}PhoneBtn`);
        const isPhoneValid = /^\d{3}-\d{4}-\d{4}$/.test(phone);
        
        if(!isPhoneButtonFlipped[section]) {
            btn.disabled = !(isPhoneValid && isUsernameValid);
        }
    }
}

// 4. 전화번호 인증 전용 분기 처리 로직
function handlePhoneAuthClick(section) {
    clearMessage(section);

    if (!isPhoneButtonFlipped[section]) {
        const radios = document.querySelectorAll(`input[name="${section}AuthType"]`);
        radios.forEach(r => r.disabled = true); 

        // 전화번호 입력창 readOnly 적용
        document.getElementById(`${section}PhoneInput`).readOnly = true;
        
        // 비밀번호 찾기 탭일 경우 아이디 입력창도 readOnly 적용
        if (section === 'pw') {
            document.getElementById('pwUsernameInput').readOnly = true;
        }

        // 랜덤 6자리 발급 및 문구 노출 처리
        const randomCode = Math.floor(100000 + Math.random() * 900000).toString();
        currentGeneratedCode[section] = randomCode;
        document.getElementById(`${section}GeneratedNum`).innerText = randomCode;

        document.getElementById(`${section}VerificationRow`).style.display = 'flex';
        document.getElementById(`${section}PhoneVerifyWrapper`).style.display = 'block';
        document.getElementById(`${section}EmailVerifyWrapper`).style.display = 'none';

        // 버튼 텍스트를 '인증완료'로 변경
        const phoneBtn = document.getElementById(`${section}PhoneBtn`);
        phoneBtn.innerText = "인증완료";
        isPhoneButtonFlipped[section] = true;
        
        // 시연용 딜레이 Mock (3초 후 전송 성공 상태 전환)
        setTimeout(() => {
            isPhoneVerifiedMock[section] = true;
        }, 3000);

    } else {
        if (isPhoneVerifiedMock[section]) {
            if (section === 'id') {
                document.getElementById('idResultContainer').style.display = 'block';
            }
            clearMessage(section);
        } else {
            showMessage(section, "인증번호가 확인되지 않았습니다 다시 시도해주세요.");
            if (section === 'id') {
                document.getElementById('idResultContainer').style.display = 'none';
            }
        }
    }
}

// 5. 이메일 인증 요청 발송 제어 및 타이머
function sendVerificationCode(section) {
    clearMessage(section);
    
    const radios = document.querySelectorAll(`input[name="${section}AuthType"]`);
    radios.forEach(r => r.disabled = true);

    // 이메일 입력창 readOnly 적용
    document.getElementById(`${section}EmailInput`).readOnly = true;
    
    // 비밀번호 찾기 탭일 경우 아이디 입력창도 readOnly 적용
    if (section === 'pw') {
        document.getElementById('pwUsernameInput').readOnly = true;
    }

    document.getElementById(`${section}VerifyInput`).value = '';
    document.getElementById(`${section}VerifyInput`).readOnly = false; // 리셋
    document.getElementById(`${section}VerificationRow`).style.display = 'flex';
    document.getElementById(`${section}EmailVerifyWrapper`).style.display = 'block';
    document.getElementById(`${section}PhoneVerifyWrapper`).style.display = 'none';

    currentGeneratedCode[section] = "123456"; 
    startEmailTimer(3 * 60, document.getElementById(`${section}Timer`));
}

// 이메일 타이머 제어 함수
function startEmailTimer(duration, displayElement) {
    clearInterval(timerInterval);
    let timer = duration, minutes, seconds;
    
    displayElement.textContent = "남은 시간 : 3분 00초";
    
    timer--;
    timerInterval = setInterval(function () {
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        seconds = seconds < 10 ? "0" + seconds : seconds;
        displayElement.textContent = "남은 시간 : " + minutes + "분 " + seconds + "초";

        if (--timer < 0) {
            clearInterval(timerInterval);
            displayElement.textContent = "인증 시간이 만료되었습니다. 다시 요청해 주세요.";
        }
    }, 1000);
}

// 이메일 전용 확인 버튼 로직
function confirmVerificationCode(section) {
    const verifyInput = document.getElementById(`${section}VerifyInput`);
    
    if(verifyInput.value.length !== 6) {
        showMessage(section, "인증번호 6자리를 정확히 입력해 주세요.");
        return;
    }

    if (verifyInput.value === currentGeneratedCode[section]) {
        clearInterval(timerInterval);
        document.getElementById(`${section}Timer`).style.display = 'none'; 
        clearMessage(section);
        
        // 이메일 인증 성공 시 인증번호 입력창 readOnly 적용
        verifyInput.readOnly = true;

        if (section === 'id') {
            document.getElementById('idResultContainer').style.display = 'block';
        }
    } else {
        showMessage(section, "인증번호가 일치하지 않습니다.");
        if (section === 'id') {
            document.getElementById('idResultContainer').style.display = 'none';
        }
    }
}

// 경고 메시지 출력 헬퍼 함수
function showMessage(section, text) {
    const msgSpan = document.getElementById(`${section}AuthMessage`);
    msgSpan.innerText = text;
    msgSpan.style.display = 'block';
}

function clearMessage(section) {
    const msgSpan = document.getElementById(`${section}AuthMessage`);
    msgSpan.innerText = '';
    msgSpan.style.display = 'none';
}

function handleFindId(e) { e.preventDefault(); return false; }
function handleFindPw(e) { e.preventDefault(); return false; }