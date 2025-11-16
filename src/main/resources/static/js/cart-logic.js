/**
 * Quản lý giỏ hàng phía client (sử dụng localStorage).
 * Tên file: cart-logic.js
 * PHIÊN BẢN SẠCH
 */
window.CartManager = {
    // Khóa lưu trong localStorage
    STORAGE_KEY: 'cart',

    /**
     * Kiểm tra xem localStorage có khả dụng không.
     */
    isStorageAvailable() {
        try {
            const test = '__storage_test__';
            localStorage.setItem(test, test);
            localStorage.removeItem(test);
            return true;
        } catch (e) {
            console.error('LocalStorage is not available:', e);
            return false;
        }
    },

    /**
     * Lấy giỏ hàng từ localStorage.
     * Luôn trả về một mảng (Array).
     */
    getCart() {
        if (!this.isStorageAvailable()) {
            return []; // Trả về mảng rỗng nếu không có storage
        }

        try {
            const cartJson = localStorage.getItem(this.STORAGE_KEY);
            // Nếu có cartJson, parse nó. Nếu không, trả về mảng rỗng.
            return cartJson ? JSON.parse(cartJson) : [];
        } catch (error) {
            console.error('Error getting cart:', error);
            // Nếu parse lỗi (dữ liệu rác), trả về mảng rỗng
            return [];
        }
    },

    /**
     * Lưu giỏ hàng vào localStorage.
     */
    saveCart(cart) {
        if (!this.isStorageAvailable()) {
            return; // Không làm gì nếu không có storage
        }

        try {
            // Lọc ra các item rỗng hoặc bị lỗi trước khi lưu
            const validCart = cart.filter(item => item && item.bookId);

            localStorage.setItem(this.STORAGE_KEY, JSON.stringify(validCart));

            // Cập nhật badge sau khi lưu thành công
            this.updateCartBadge();
        } catch (error) {
            console.error('Error saving cart:', error);
        }
    },

    /**
     * HÀM QUAN TRỌNG: Thêm một sản phẩm vào giỏ hàng.
     * (Hàm chuẩn có 4 tham số)
     */
    addToCart(bookId, title, price, quantity = 1) {

        // --- BƯỚC 1: LÀM SẠCH DỮ LIỆU ĐẦU VÀO ---
        const safeBookId = String(bookId || Date.now());
        const safeTitle = title || 'Sách không tên';
        const safePrice = parseFloat(price) || 0;
        const safeQuantity = parseInt(quantity) || 1;

        // In ra console để debug
        console.log('[CartManager] Đang thêm vào giỏ:', {
            id: safeBookId,
            title: safeTitle,
            price: safePrice,
            qty: safeQuantity
        });

        // --- BƯỚC 2: XỬ LÝ GIỎ HÀNG ---
        const cart = this.getCart();
        const existingItem = cart.find(item => String(item.bookId) === safeBookId);

        if (existingItem) {
            // Nếu sách đã tồn tại, cập nhật số lượng
            existingItem.quantity += safeQuantity;
            existingItem.title = safeTitle; // Cập nhật lại tên/giá
            existingItem.price = safePrice;
        } else {
            // Nếu sách chưa có, thêm mới
            cart.push({
                bookId: safeBookId,
                title: safeTitle,
                price: safePrice,
                quantity: safeQuantity
            });
        }

        // --- BƯỚC 3: LƯU LẠI ---
        this.saveCart(cart);

        return cart;
    },

    /**
     * Cập nhật số lượng của một sản phẩm.
     */
    updateQuantity(bookId, newQuantity) {
        const safeBookId = String(bookId);
        const safeNewQuantity = parseInt(newQuantity);

        if (isNaN(safeNewQuantity) || safeNewQuantity < 1) {
            // Nếu số lượng mới không hợp lệ hoặc < 1, xóa sản phẩm
            return this.removeFromCart(safeBookId);
        }

        const cart = this.getCart();
        const item = cart.find(item => String(item.bookId) === safeBookId);

        if (item) {
            item.quantity = safeNewQuantity;
            this.saveCart(cart);
        }

        return cart;
    },

    /**
     * Xóa một sản phẩm khỏi giỏ hàng.
     */
    removeFromCart(bookId) {
        const safeBookId = String(bookId);
        const cart = this.getCart().filter(item => String(item.bookId) !== safeBookId);
        this.saveCart(cart);
        return cart;
    },

    /**
     * Xóa sạch giỏ hàng.
     */
    clearCart() {
        this.saveCart([]); // Lưu một mảng rỗng
        console.log('[CartManager] Giỏ hàng đã được xóa.');
    },

    /**
     * Lấy tổng số lượng sản phẩm.
     */
    getTotalItems() {
        return this.getCart().reduce((total, item) => {
            const qty = parseInt(item.quantity) || 0;
            return total + qty;
        }, 0);
    },

    /**
     * Cập nhật số hiển thị (badge) trên icon giỏ hàng.
     */
    updateCartBadge() {
        try {
            const badge = document.getElementById('cartBadge');
            if (!badge) return;

            const totalItems = this.getTotalItems();

            if (totalItems > 0) {
                badge.textContent = totalItems;
                badge.style.display = 'inline';
            } else {
                badge.style.display = 'none';
            }
        } catch (error) {
            console.error('Error updating cart badge:', error);
        }
    }
};

/**
 * Khởi tạo: Cập nhật cart badge ngay khi trang được tải.
 */
document.addEventListener('DOMContentLoaded', function() {
    if (window.CartManager) {
        window.CartManager.updateCartBadge();
    }
});