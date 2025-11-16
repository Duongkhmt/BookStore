// API Helper functions
class API {
    static async authFetch(url, options = {}) {
        const token = localStorage.getItem('token');

        // Merge headers
        options.headers = {
            ...options.headers,
            'Authorization': token ? `Bearer ${token}` : ''
        };

        // Add Content-Type for requests with body
        if (options.body && !options.headers['Content-Type']) {
            options.headers['Content-Type'] = 'application/json';
        }

        const res = await fetch(url, options);

        if (res.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/login';
            throw new Error('Unauthorized');
        }

        return res;
    }

    static async get(url) {
        return this.authFetch(url);
    }

    static async post(url, data) {
        return this.authFetch(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    static async put(url, data) {
        return this.authFetch(url, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    static async delete(url) {
        return this.authFetch(url, {
            method: 'DELETE'
        });
    }

    static async upload(url, formData) {
        return this.authFetch(url, {
            method: 'POST',
            body: formData,
            headers: {
                'Authorization': localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
            }
        });
    }
}

// Cart management
class CartManager {
    static getCart() {
        return JSON.parse(localStorage.getItem('cart') || '[]');
    }

    static addToCart(bookId, title, price, quantity = 1) { // <-- Sửa tham số ở đây
        const cart = this.getCart();
        const safeBookId = String(bookId); // Đảm bảo an toàn
        const existingItem = cart.find(item => String(item.bookId) === safeBookId);

        // In ra để debug
        console.log('[CartManager] Đang thêm:', { bookId, title, price, quantity });

        if (existingItem) {
            existingItem.quantity += quantity;
            // Cập nhật lại info nếu cần
            existingItem.title = title || 'Sách không tên';
            existingItem.price = price || 0;
        } else {
            cart.push({
                bookId: safeBookId,
                title: title || 'Sách không tên',
                price: price || 0,
                image: null, // File catalog.html không gửi 'image', nên để null
                quantity: quantity
            });
        }

        localStorage.setItem('cart', JSON.stringify(cart));
        this.updateCartBadge();
        return cart;
    }

    static removeFromCart(bookId) {
        const cart = this.getCart().filter(item => item.bookId !== bookId);
        localStorage.setItem('cart', JSON.stringify(cart));
        this.updateCartBadge();
        return cart;
    }

    static updateQuantity(bookId, quantity) {
        const cart = this.getCart();
        const item = cart.find(item => item.bookId === bookId);

        if (item) {
            if (quantity <= 0) {
                return this.removeFromCart(bookId);
            }
            item.quantity = quantity;
        }

        localStorage.setItem('cart', JSON.stringify(cart));
        this.updateCartBadge();
        return cart;
    }

    static clearCart() {
        localStorage.removeItem('cart');
        this.updateCartBadge();
    }

    static getTotalItems() {
        return this.getCart().reduce((total, item) => total + (item.quantity || 1), 0);
    }

    static getTotalPrice() {
        return this.getCart().reduce((total, item) => total + (item.price || 0) * (item.quantity || 1), 0);
    }

    static updateCartBadge() {
        const badge = document.getElementById('cartBadge');
        if (badge) {
            const totalItems = this.getTotalItems();
            badge.textContent = totalItems;
            badge.style.display = totalItems > 0 ? 'inline-block' : 'none';
        }
    }
}

// Utility functions
const Utils = {
    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    },

    formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('vi-VN');
    },

    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type}`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
            min-width: 300px;
            animation: slideIn 0.3s ease;
        `;
        notification.textContent = message;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => notification.remove(), 300);
        }, 3000);
    }
};

// Thêm CSS cho animation notification
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
`;
document.head.appendChild(style);

// Initialize cart badge on page load
document.addEventListener('DOMContentLoaded', () => {
    CartManager.updateCartBadge();
});

// Export for use in other modules
window.API = API;
window.CartManager = CartManager;
window.Utils = Utils;