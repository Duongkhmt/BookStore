/**
 * File: auth.js
 * Quản lý xác thực, phân quyền (guard) và tự động thêm token (interceptor).
 */

// === 1. HÀM DECODE TOKEN ===
function decodeJWT(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error('Error decoding JWT:', e);
        return null;
    }
}

/// === 2. HÀM LẤY ROLE (SỬA LẠI) ===
// === 2. HÀM LẤY ROLE (SỬA LẠI) ===
function getUserRole() {
    const token = localStorage.getItem('token');
    if (!token) {
        console.log('❌ No token found');
        return null;
    }

    const decoded = decodeJWT(token);
    if (!decoded) {
        console.log('❌ Cannot decode token');
        return null;
    }

    console.log('🔍 Decoded token:', decoded);

    if (decoded.authorities) {
        const authorities = decoded.authorities;
        const authArray = typeof authorities === 'string'
            ? authorities.split(',')
            : (Array.isArray(authorities) ? authorities : [authorities]);
        const hasAdmin = authArray.some(auth => auth.includes('ADMIN') || auth === 'ROLE_ADMIN');
        return hasAdmin ? 'ADMIN' : 'USER';
    }

    // Fallback
    if (decoded.sub && decoded.sub.toLowerCase() === 'admin') {
        console.log('✅ Role: ADMIN (from username)');
        return 'ADMIN';
    }

    console.log('✅ Role: USER (default)');
    return 'USER';
}


// === 3. HÀM BẢO VỆ TRANG (STRICT VERSION) ===
function checkRoleAccess() {
    const token = localStorage.getItem('token');
    const userRole = getUserRole();
    const currentPath = window.location.pathname;

    console.log(`🔐 Strict Auth Check: Path=${currentPath}, Role=${userRole}`);

    // PUBLIC PAGES - ai cũng vào được
    const publicPages = ['/login', '/register', '/', '/catalog'];
    if (publicPages.includes(currentPath)) {
        console.log('✅ Public page - Access granted');
        return;
    }

    // KHÔNG CÓ TOKEN -> về login
    if (!token) {
        console.warn('🚨 No token - Redirecting to login');
        window.location.href = '/login';
        return;
    }

    // ADMIN: CHỈ được vào /admin/**
    if (userRole === 'ADMIN') {
        if (currentPath.startsWith('/admin')) {
            console.log('✅ Admin accessing admin area - Access granted');
            return;
        } else {
            console.warn('🚫 Admin cannot access user pages - Redirecting to /admin');
            window.location.href = '/admin';
            return;
        }
    }

    // USER: CHỈ được vào user pages, KHÔNG được vào admin
    if (userRole === 'USER') {
        const allowedUserPages = ['/orders', '/cart', '/dashboard', '/catalog'];
        const isAllowed = allowedUserPages.some(page =>
            currentPath === page || currentPath.startsWith(page + '/')
        );

        if (isAllowed) {
            console.log('✅ User accessing allowed page - Access granted');
            return;
        } else if (currentPath.startsWith('/admin')) {
            console.warn('🚫 User cannot access admin area - Redirecting to /catalog');
            window.location.href = '/catalog';
            return;
        } else {
            console.warn('🚫 User cannot access this page - Redirecting to /catalog');
            window.location.href = '/catalog';
            return;
        }
    }

    // FALLBACK
    console.error('🚨 Invalid role - Clearing token and redirecting to login');
    localStorage.removeItem('token');
    window.location.href = '/login';
}

// === 4. TỰ ĐỘNG THÊM TOKEN (FETCH INTERCEPTOR) ===
// Ghi đè hàm fetch gốc
const originalFetch = window.fetch;
window.fetch = async function(...args) {
    const [url, options = {}] = args;

    // Chỉ thêm token cho API calls
    if (typeof url === 'string' && url.startsWith('/api/')) {
        const token = localStorage.getItem('token');
        if (token) {
            options.headers = {
                ...options.headers,
                'Authorization': `Bearer ${token}`
            };
        }
        if (['POST', 'PUT', 'PATCH'].includes(options.method)) {
            options.headers = {
                'Content-Type': 'application/json',
                ...options.headers
            };
        }
    }
    return originalFetch(url, options);
};

// === 5. CHẠY BẢO VỆ KHI TẢI TRANG ===
document.addEventListener('DOMContentLoaded', function() {
    checkRoleAccess();
});

// === 6. EXPORT (Để layout.html có thể dùng) ===
window.getUserRole = getUserRole;
window.checkRoleAccess = checkRoleAccess;
window.decodeJWT = decodeJWT;