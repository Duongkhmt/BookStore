/* File: src/main/resources/static/js/dashboard.js */

document.addEventListener('DOMContentLoaded', async function() {
    await loadDashboardData();
});

async function loadDashboardData() {
    try {
        // Load thống kê
        const [booksRes, ordersRes, usersRes] = await Promise.all([
            API.get('/api/books'),
            API.get('/api/orders'),
            API.get('/api/admin/users')
        ]);

        const books = (await booksRes.json())?.data || [];
        const orders = (await ordersRes.json())?.data || [];
        const users = (await usersRes.json())?.data || [];

        // Update thống kê
        document.getElementById('statBooks').textContent = books.length;
        document.getElementById('statOrders').textContent = orders.length;
        document.getElementById('statUsers').textContent = users.length;

        const revenue = orders.reduce((sum, o) => sum + (parseFloat(o.totalAmount) || 0), 0);
        document.getElementById('statRevenue').textContent = Utils.formatPrice(revenue);

        // Load đơn hàng gần đây
        await loadRecentOrders(orders.slice(0, 5));

        // Load sách sắp hết hàng
        const lowStockBooks = books.filter(b => b.stockQuantity < 10).slice(0, 5);
        await loadLowStockBooks(lowStockBooks);

    } catch (error) {
        console.error('Lỗi tải dashboard:', error);
        // Utils đã được định nghĩa bên layout.html nên file này vẫn gọi được bình thường
        if (typeof Utils !== 'undefined') {
            Utils.showNotification('Lỗi tải dữ liệu dashboard', 'error');
        }
    }
}

async function loadRecentOrders(orders) {
    const container = document.getElementById('recentOrders');

    if (orders.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #64748b; padding: 2rem;">Không có đơn hàng nào</p>';
        return;
    }

    // Bây giờ viết HTML thoải mái, không sợ Thymeleaf bắt lỗi
    const html = `
        <table>
            <thead>
                <tr>
                    <th>Mã đơn</th>
                    <th>Khách hàng</th>
                    <th>Tổng tiền</th>
                    <th>Trạng thái</th>
                </tr>
            </thead>
            <tbody>
                ${orders.map(order => `
                    <tr>
                        <td>#${order.id}</td>
                        <td>${order.username || 'N/A'}</td>
                        <td>${Utils.formatPrice(order.totalAmount)}</td>
                        <td>
                            <span class="badge ${getOrderStatusBadge(order.status)}">
                                ${getOrderStatusText(order.status)}
                            </span>
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    container.innerHTML = html;
}

async function loadLowStockBooks(books) {
    const container = document.getElementById('lowStockBooks');

    if (books.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: #64748b; padding: 2rem;">Không có sách nào sắp hết hàng</p>';
        return;
    }

    const html = `
        <div style="display: flex; flex-direction: column; gap: 0.75rem;">
            ${books.map(book => `
                <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.75rem; background: #f8fafc; border-radius: 0.5rem;">
                    <div>
                        <div style="font-weight: 500;">${book.title}</div>
                        <div style="font-size: 0.875rem; color: #64748b;">${book.author || 'N/A'}</div>
                    </div>
                    <div style="text-align: right;">
                        <div style="font-size: 0.875rem; color: #64748b;">Tồn kho</div>
                        <div style="color: #ef4444; font-weight: 600;">${book.stockQuantity}</div>
                    </div>
                </div>
            `).join('')}
        </div>
    `;

    container.innerHTML = html;
}

function getOrderStatusBadge(status) {
    const badges = {
        'PENDING': 'badge-warning',
        'PROCESSING': 'badge-info',
        'SHIPPED': 'badge-info',
        'DELIVERED': 'badge-success',
        'CANCELLED': 'badge-danger'
    };
    return badges[status] || 'badge-info';
}

function getOrderStatusText(status) {
    const texts = {
        'PENDING': 'Chờ xử lý',
        'PROCESSING': 'Đang xử lý',
        'SHIPPED': 'Đang giao',
        'DELIVERED': 'Đã giao',
        'CANCELLED': 'Đã hủy'
    };
    return texts[status] || status;
}