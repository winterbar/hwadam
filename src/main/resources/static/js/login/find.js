let timerInterval;

// 이메일 유효성 검사
function validateEmail() {
    const input = document.getElementById('emailInput');
    const btn = document.getElementById('emailBtn');
    // 이메일 정규식 : 아이디@도메인 형식인지 확인
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    // 유효성 검사로 버튼 활성화 여부 설정
    if(emailRegex.test(input.value)) {
        btn.disabled = false;
    } else {
        btn.disabled = true;
    }
}

// 인증번호 6자리 입력 시 확인 버튼 활성화
function validateVerifyBtn() {
    const code = document.getElementById('verifyCode').value;
    const btn = document.getElementById('verifyBtn');
    btn.disabled = code.length !== 6; // 6자리일때만 활성화
}

// 타이머 시작
function startTimer(duration) {
    let timer = duration, minutes, seconds;
    const display = document.getElementById('timerDisplay');
    display.style.display = 'block';

    // 1초마다 갱신, 인증 시간 만료 시 확인 버튼 비활성화
    timerInterval = setInterval(() => {
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);
        display.textContent = `남은 시간 : ${minutes}분 ${seconds < 10 ? '0' + seconds : seconds}초`;
        
        if(--timer < 0) {
            clearInterval(timerInterval);
            display.textContent = "인증 시간이 만료되었습니다.";
            document.getElementById('verifyBtn').disabled = true;
        }
    
    }, 1000);
}

// 토스트 출력
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    
    // 기존 클래스 제거
    toast.className = 'toast';
    // 메시지 설정
    toast.textContent = message;
    // success나 error 추가
    toast.classList.add(type);
    // 출력
    toast.classList.add('show');
    // 3초 후 숨기기
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// 이메일 인증 요청을 서버에게 보낼 때 이벤트
// 요청 후 화면이 1~3초간 멈추는 현상을 방지하기 위해 fetch를 활용해 비동기 처리하기로 함
document.addEventListener('DOMContentLoaded', function() {
    const emailForm = document.getElementById('emailForm');
    const emailBtn = document.getElementById('emailBtn');
    const emailInput = document.getElementById('emailInput');
    const hiddenEmailInput = document.getElementById('hiddenEmailInput');

    emailForm.addEventListener('submit', async function(e) { // 함수 비동기화 (async)
        e.preventDefault(); // 페이지 이동 방지

        emailBtn.disabled = true;
        emailBtn.innerText = "전송 중..";
        emailInput.readOnly = true;
        
        // 인증번호 입력 영역과 남은시간 출력 영역
        document.getElementById('verifyForm').style.display = 'flex';
        document.getElementById('timerDisplay').style.display = 'block';
        startTimer(300); // 5분

        // fetch() 요청에 대한 응답이 올때까지 await 위치에서 기다림
        // await는 함수 내부만 멈추고 JS 전체를 멈추지 않음
        try {
            const response = await fetch(this.action, {
                method: 'POST',
                body: new FormData(this)
            });

            const data = await response.json();

            if(response.ok) {
                emailBtn.innerText = '전송 완료';
                hiddenEmailInput.value = emailInput.value;
                showToast(data.message, 'success');
            } else {
                clearInterval(timerInterval);
                document.getElementById('timerDisplay').style.display ='none';
                document.getElementById('verifyForm').style.display = 'none';
                showToast(data.message, 'error');

                // 실패에 따른 상태복구
                emailBtn.disabled = false;
                emailBtn.innerText = '인증 요청';
                emailInput.readOnly = false;
            }
        } catch (error) {
            clearInterval(timerInterval);
            document.getElementById('timerDisplay').style.display ='none';
            document.getElementById('verifyForm').style.display = 'none';
            showToast("서버와 연결할 수 없습니다. 나중에 다시 시도해주세요.", 'error');

            // 실패에 따른 상태복구
            emailBtn.disabled = false;
            emailBtn.innerText = '인증 요청';
            emailInput.readOnly = false;
        }
    });
});