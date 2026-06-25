// 프로필 편집 버튼을 눌렀을 때 모달을 여는 기능
function openProfileModal() {
    const modal = document.getElementById('profileModal');
    document.getElementById('fileInfo').style.display = 'none';
    document.getElementById('saveBtn').disabled = true;
    if (modal) {
        modal.style.display = 'flex';
        saveBtn.disabled = true;
    }
}

// 저장 버튼을 눌렀을 때 모달을 닫는 기능
function closeProfileModal() {
    const modal = document.getElementById('profileModal');
    if (modal) {
        modal.style.display = 'none';
    }

    if (fileInput) {
        fileInput.value = ""; 
    }
    
    if (fileInfo) {
        fileInfo.style.display = 'none';
        document.getElementById('fileName').textContent = "";
        document.getElementById('fileSize').textContent = "";
    }
}

// 회원정보 수정, 비밀번호 변경을 눌렀을 때 모달을 여는 기능
function openPasswordModal(url) {
    document.getElementById('passwordConfirmModal').style.display = 'flex';
    document.getElementById('path').value = url; // 목적지 설정
}

// 확인 버튼을 눌렀을 때 모달을 닫는 기능
function closePasswordModal() {
    document.getElementById('passwordConfirmModal').style.display = 'none';
    document.getElementById('passwordForm').reset();
    document.getElementById('passwordError').style.display = 'none';
}

// ESC 키로 모달을 닫는 기능
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape' || event.keyCode === 27) {
        const profileModal = document.getElementById('profileModal');
        const passwordConfirmModal = document.getElementById('passwordConfirmModal');
        
        if (profileModal.style.display === 'flex') {
            closeProfileModal();
        }
        
        if (passwordConfirmModal.style.display === 'flex') {
            closePasswordModal();
        }
    }
});

// 비밀번호 불일치 시, 페이지가 로드되면 모달을 실행
window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('error') === 'true') { // 비밀번호가 맞지 않다면 에러가 발생
        document.getElementById('passwordConfirmModal').style.display = 'flex';
        document.getElementById('passwordError').style.display = 'block';

        const savedUrl = urlParams.get('path');
        if(savedUrl) {
            document.getElementById('path').value = savedUrl;
        }
    }
};

// 수정 성공 여부를 알려주는 토스트 알림
function showToast(message) {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.className = "toast show";
    
    // 3초 뒤에 토스트 숨김
    setTimeout(function() {
        toast.className = toast.className.replace("show", "");
    }, 3000);
}

// 스크립트 실행 전 브라우저가 모두 로딩될 때까지 기다림
document.addEventListener("DOMContentLoaded", () => {
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const fileInfo = document.getElementById('fileInfo');
    const fileName = document.getElementById('fileName');
    const fileSize = document.getElementById('fileSize');
    const saveBtn = document.getElementById('saveBtn');

    // 클릭 시 파일 선택
    dropZone.onclick = () => fileInput.click();

    // 드래그 앤 드롭 이벤트
    dropZone.ondragover = (e) => { e.preventDefault(); dropZone.style.borderColor = '#FA6E68'; };
    dropZone.ondragleave = () => dropZone.style.borderColor = '#ccc';
    dropZone.ondrop = (e) => {
        e.preventDefault();
        handleFiles(e.dataTransfer.files);
    };

    fileInput.onchange = (e) => handleFiles(e.target.files);

    function handleFiles(files) {
        if (files.length === 0) return;
        const file = files[0];
        
        // 이미지 파일만 등록 가능
        if (!file.type.startsWith('image/')) {
            alert("이미지 파일만 등록 가능합니다.");
            return;
        }
        
        // 파일 크기는 5MB 이하 제한
        if (file.size > 5 * 1024 * 1024) {
            alert("파일 크기는 5MB를 초과할 수 없습니다.");
            return;
        }

        // 드래그 앤 드롭으로 등록된 파일을 할당
        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(file);
        fileInput.files = dataTransfer.files;

        // 등록한 이미지 파일 정보 출력
        document.getElementById('fileName').textContent = file.name;
        document.getElementById('fileSize').textContent = (file.size / 1024 / 1024).toFixed(2) + " MB";
        document.getElementById('fileInfo').style.display = 'block';

        saveBtn.disabled = false; // 파일 선택 시 저장 버튼 활성화
    }
});

