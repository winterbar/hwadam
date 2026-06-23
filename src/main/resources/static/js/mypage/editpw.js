const newPw = document.getElementById('newPw');
const confirmPw = document.getElementById('confirmPw');
const msg = document.getElementById('msg');
const saveBtn = document.getElementById('saveBtn');

// 글자를 입력할 때마다 새로운 비밀번호와의 일치 여부 확인
function validatePassword() {
    const p1 = newPw.value;
    const p2 = confirmPw.value;

    if (p2 === "") {
        msg.className = "validation-msg";
        saveBtn.disabled = true;
        return;
    }

    if (p1 === p2) {
        msg.textContent = "비밀번호가 일치합니다.";
        msg.className = "validation-msg msg-match";
        saveBtn.disabled = false;
    } else {
        msg.textContent = "비밀번호가 일치하지 않습니다.";
        msg.className = "validation-msg msg-mismatch";
        saveBtn.disabled = true;
    }
}

newPw.addEventListener('input', validatePassword);
confirmPw.addEventListener('input', validatePassword);