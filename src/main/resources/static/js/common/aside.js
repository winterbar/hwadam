// 보안을 위한 로그인된 사용자의 아이디 마스킹
document.addEventListener("DOMContentLoaded", function() {
    const idElement = document.getElementById("masked-id");
    
    if (idElement) {
        const rawId = idElement.getAttribute("data-username") ? idElement.getAttribute("data-username").trim() : ""; 
        
        if (rawId.length > 0) {
            const visiblePart = rawId.substring(0, 4);
            const maskedPart = "*".repeat(Math.max(0, rawId.length - 4));
            
            idElement.textContent = `(${visiblePart}${maskedPart})`;
        }
    }
});