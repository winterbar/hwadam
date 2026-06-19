// 프로필 편집 버튼을 눌렀을 때 모달을 여는 기능
function openProfileModal() {
    const modal = document.getElementById('profileModal');
    if (modal) {
        modal.style.display = 'flex';
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

// 스크립트 실행 전 브라우저가 모두 로딩될 때까지 기다림
document.addEventListener("DOMContentLoaded", () => {
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const fileInfo = document.getElementById('fileInfo');
    const fileName = document.getElementById('fileName');
    const fileSize = document.getElementById('fileSize');

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

        // 등록한 이미지 파일 정보 출력
        document.getElementById('fileName').textContent = file.name;
        document.getElementById('fileSize').textContent = (file.size / 1024 / 1024).toFixed(2) + " MB";
        document.getElementById('fileInfo').style.display = 'block';
        
        document.getElementById('fileInput').files = files;
    }
});

