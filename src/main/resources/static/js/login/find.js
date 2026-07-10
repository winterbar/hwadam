let timerInterval; // 남은시간 타이머

// 아이디 찾기 <-> 비밀번호 찾기 탭 전환
function switchFindType(type) {
    document.querySelectorAll('.find-type-tab').forEach(tab => {
        tab.classList.remove('active');
    })
    if(type === 'id') {
        document.querySelectorAll('.find-type-tab')[0].classList.add('active');
        document.getElementById('findIdSection').style.display = 'block';
        document.getElementById('findPwSection').style.display = 'none';
        
        // 이메일 입력 초기화
        document.getElementById('emailInputId').value = "";
        document.getElementById('emailInputId').readOnly = false;
        document.getElementById('emailBtnId').disabled = true;
        document.getElementById('emailBtnId').innerText = '인증 요청';
        // 인증번호 입력 + 타이머 초기화 및 숨김
        document.getElementById('timerDisplayId').style.display = 'none';
        document.getElementById('timerDisplayId').value= "";
        clearInterval(timerInterval);
        document.getElementById('verifyFormId').style.display = 'none';
        document.getElementById('verifyCodeId').value = "";
        document.getElementById('verifyCodeId').readOnly = false;
        document.getElementById('verifyBtnId').disabled = true;
        // 결과창 숨김
        document.getElementById('resultContainerId').style.display = 'none';
    } else {
        document.querySelectorAll('.find-type-tab')[1].classList.add('active');
        document.getElementById('findIdSection').style.display = 'none';
        document.getElementById('findPwSection').style.display = 'block';

        // 아이디 및 이메일 입력 초기화
        document.getElementById('idInput').value = "";
        document.getElementById('idInput').readOnly = false;
        document.getElementById('emailInputPw').value = "";
        document.getElementById('emailInputPw').readOnly = false;
        document.getElementById('emailBtnPw').disabled = true;
        document.getElementById('emailBtnPw').innerText = '인증 요청';
        // 인증번호 입력 + 타이머 초기화 및 숨김
        document.getElementById('timerDisplayPw').style.display = 'none';
        document.getElementById('timerDisplayPw').value= "";
        clearInterval(timerInterval);
        document.getElementById('verifyFormPw').style.display = 'none';
        document.getElementById('verifyCodePw').value = "";
        document.getElementById('verifyCodePw').readOnly = false;
        document.getElementById('verifyBtnPw').disabled = true;
        // 결과창 숨김
        document.getElementById('resultContainerPw').style.display = 'none';
    }
}

// 이메일 유효성 검사
function validateEmail(type) {
    // 이메일 정규식 : 아이디@도메인 형식인지 확인
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    let input, btn
    if(type === 'id') {
        input = document.getElementById('emailInputId');
        btn = document.getElementById('emailBtnId');
    } else {
        input = document.getElementById('emailInputPw');
        btn = document.getElementById('emailBtnPw');
    }

    let isUsernameValid = true;
    if(type == 'pw') {
        const usernameInput = document.getElementById('idInput');
        isUsernameValid = usernameInput.value.trim().length > 0; // 한 글자라도 쳤는지 확인
    }
    // 유효성 검사로 버튼 활성화 여부 설정
    if(emailRegex.test(input.value) && isUsernameValid) {
        btn.disabled = false;
    } else {
        btn.disabled = true;
    }
}

// 인증번호 6자리 입력 시 확인 버튼 활성화
function validateVerifyBtn(type) {
    let code, btn;
    if(type === 'id') {
        code = document.getElementById('verifyCodeId').value;
        btn = document.getElementById('verifyBtnId');
    } else {
        code = document.getElementById('verifyCodePw').value;
        btn = document.getElementById('verifyBtnPw');
    }
    btn.disabled = code.length !== 6; // 6자리일때만 활성화
}

// 타이머 시작
function startTimer(duration, type) {
    let timer = duration, minutes, seconds;
    let display;
    if(type === 'id') {
        display = document.getElementById('timerDisplayId');
    } else {
        display = document.getElementById('timerDisplayPw');
    }
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

document.addEventListener('DOMContentLoaded', function() {
    const emailFormId = document.getElementById('emailFormId');
    const emailBtnId = document.getElementById('emailBtnId');
    const emailInputId = document.getElementById('emailInputId');
    const hiddenEmailInputId = document.getElementById('hiddenEmailInputId');
    const verifyFormId = document.getElementById('verifyFormId');
    const verifyCodeInputId = document.getElementById('verifyCodeId');
    const verifyBtnId = document.getElementById('verifyBtnId');

    const emailFormPw = document.getElementById('emailFormPw');
    const emailBtnPw = document.getElementById('emailBtnPw');
    const idInput = document.getElementById('idInput');
    const emailInputPw = document.getElementById('emailInputPw');
    const hiddenIdInput = document.getElementById('hiddenIdInput');
    const hiddenEmailInputPw = document.getElementById('hiddenEmailInputPw');
    const verifyFormPw = document.getElementById('verifyFormPw');
    const verifyCodeInputPw = document.getElementById('verifyCodePw');
    const verifyBtnPw = document.getElementById('verifyBtnPw');

    // 현재 url의 쿼리 스트링 파라미터를 가져옴
    const urlParams = new URLSearchParams(window.location.search);
    const typeParam = urlParams.get('type'); // id or pw

    if(typeParam === 'pw') {
        switchFindType('pw');
    } else {
        switchFindType('id'); // 기본 값
    }

    //* 아이디 찾기 *//
    // 이메일 인증 요청을 서버에게 보내는 이벤트
    // 요청 후 화면이 1~3초간 멈추는 현상을 방지하기 위해 fetch를 활용해 비동기 처리
    emailFormId.addEventListener('submit', async function(e) { // 비동기화
        e.preventDefault(); // 페이지 이동 방지

        emailBtnId.disabled = true;
        emailBtnId.innerText = "전송 중..";
        emailInputId.readOnly = true;
        
        // 인증번호 입력 영역과 남은시간 출력 영역
        verifyFormId.style.display = 'flex';
        document.getElementById('timerDisplayId').style.display = 'block';
        startTimer(300, 'id'); // 5분

        // fetch() 요청에 대한 응답이 올때까지 await 위치에서 기다림
        // await는 함수 내부만 멈추고 JS 전체를 멈추지 않음
        try {
            const response = await fetch(this.action, {
                method: 'POST',
                body: new FormData(this)
            });

            const data = await response.json();

            if(response.ok) {
                emailBtnId.innerText = '전송 완료';
                hiddenEmailInputId.value = emailInputId.value;
                showToast(data.message, 'success');
            } else {
                clearInterval(timerInterval);
                document.getElementById('timerDisplayId').style.display ='none';
                document.getElementById('verifyFormId').style.display = 'none';
                showToast(data.message, 'error');

                // 실패에 따른 상태복구
                emailBtnId.disabled = false;
                emailBtnId.innerText = '인증 요청';
                emailInputId.readOnly = false;
            }
        } catch (error) {
            clearInterval(timerInterval);
            document.getElementById('timerDisplayId').style.display ='none';
            document.getElementById('verifyFormId').style.display = 'none';
            showToast('서버와 연결할 수 없습니다. 나중에 다시 시도해주세요.', 'error');

            // 실패에 따른 상태복구
            emailBtnId.disabled = false;
            emailBtnId.innerText = '인증 요청';
            emailInputId.readOnly = false;
        }
    });

    // 인증번호 확인 요청을 서버에게 보내는 이벤트
    verifyFormId.addEventListener('submit', async function(e) { // 비동기화
        e.preventDefault(); // 페이지 이동 방지

        verifyBtnId.disabled = true;
        verifyCodeInputId.readOnly = true;

        try {
            const response = await fetch(this.action, {
                method: 'POST',
                body: new FormData(this)
            });

            const result = await response.json();

            if(response.ok && result.members != null) {
                showToast(result.message, 'success');

                const tableBody = document.getElementById('idTableBody');
                tableBody.innerHTML = ""; // 기존 데이터 초기화

                // 서버가 보낸 회원 목록(members) 출력
                result.members.forEach(member => {
                    const tr = document.createElement('tr');
                    
                    // 가입일 "20XX-XX-XX" 형태로 포맷팅
                    const dateObj = new Date(member.signedAt);
                    const year = dateObj.getFullYear();
                    const month = String(dateObj.getMonth() + 1).padStart(2, '0');
                    const day = String(dateObj.getDate()).padStart(2, '0');
                    const formattedDate = `${year}-${month}-${day}`;
                    
                    tr.innerHTML = `
                        <td class="account-id">${member.username}</td>
                        <td class="account-date">${formattedDate}</td>
                    `
                    tableBody.appendChild(tr);
                })
                document.getElementById('timerDisplayId').style.display = 'none';
                document.getElementById('resultContainerId').style.display = 'block';
            } else {
                showToast(result.message, 'error');
                verifyBtnId.disabled = false;
                verifyCodeInputId.readOnly = false;
                verifyCodeInputId.value = "";
            }

        } catch (error) {
            showToast('서버와 연결할 수 없습니다. 나중에 다시 시도해주세요.', 'error');
            verifyBtnId.disabled = false;
            verifyCodeInputId.readOnly = false;
            verifyCodeInputId.value = "";
        }
    });

    //* 비밀번호 찾기 *//
    // 이메일 인증 요청을 서버에게 보내는 이벤트
    emailFormPw.addEventListener('submit', async function(e) {
        e.preventDefault(); // 페이지 이동 방지

        emailBtnPw.disabled = true;
        emailBtnPw.innerText = "전송 중..";
        idInput.readOnly = true;
        emailInputPw.readOnly = true;

        // 인증번호 입력 영역과 남은시간 출력 영역
        verifyFormPw.style.display = 'flex';
        document.getElementById('timerDisplayPw').style.display = 'block';
        startTimer(300, 'pw'); // 5분

        // fetch() 요청에 대한 응답이 올때까지 await 위치에서 기다림
        // await는 함수 내부만 멈추고 JS 전체를 멈추지 않음
        try {
            const response = await fetch(this.action, {
                method: 'POST',
                body: new FormData(this)
            });

            const data = await response.json();

            if(response.ok) {
                emailBtnPw.innerText = '전송 완료';
                hiddenIdInput.value = idInput.value;
                hiddenEmailInputPw.value = emailInputPw.value;
                showToast(data.message, 'success');
            } else {
                clearInterval(timerInterval);
                document.getElementById('timerDisplayPw').style.display ='none';
                document.getElementById('verifyFormPw').style.display = 'none';
                showToast(data.message, 'error');

                // 실패에 따른 상태복구
                emailBtnPw.disabled = false;
                emailBtnPw.innerText = '인증 요청';
                idInput.readOnly = false;
                emailInputPw.readOnly = false;
            }
        } catch (error) {
            clearInterval(timerInterval);
            document.getElementById('timerDisplayPw').style.display ='none';
            document.getElementById('verifyFormPw').style.display = 'none';
            showToast('서버와 연결할 수 없습니다. 나중에 다시 시도해주세요.', 'error');

            // 실패에 따른 상태복구
            emailBtnPw.disabled = false;
            emailBtnPw.innerText = '인증 요청';
            idInput.readOnly = false;
            emailInputPw.readOnly = false;
        }
    });

    // 인증번호 확인 요청을 서버에게 보내는 이벤트
    verifyFormPw.addEventListener('submit', async function(e) {
        e.preventDefault(); // 페이지 이동 방지

        verifyBtnPw.disabled = true;
        verifyCodeInputPw.readOnly = true;
        showToast('메일을 전송중입니다.', 'waiting');

        try {
            const response = await fetch(this.action, {
                method: 'POST',
                body: new FormData(this)
            })

            const data = await response.json();

            if(response.ok && data.isSent) {
                showToast(data.message, 'success');
                document.getElementById('timerDisplayPw').style.display = 'none';
                document.getElementById('resultContainerPw').style.display = 'block';
            } else {
                showToast(data.message, 'error');
                verifyBtnPw.disabled = false;
                verifyCodeInputPw.readOnly = false;
                verifyCodeInputPw.value = "";
            }

        } catch (error) {
            showToast('서버와 연결할 수 없습니다. 나중에 다시 시도해주세요.', 'error');
            verifyBtnPw.disabled = false;
            verifyCodeInputPw.readOnly = false;
            verifyCodeInputPw.value = "";
        }
    });
});