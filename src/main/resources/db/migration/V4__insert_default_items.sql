-- MAIN DISH
INSERT INTO items (name, type, status, sold, price, description) VALUES
('Bún nước Tôm Bò', 'MAIN', 'ACTIVE', 0, 35000, 'Bún nước, Tôm, Bò'),
('Bún nước Thập Cẩm', 'MAIN', 'ACTIVE', 0, 45000, 'Bún nước, Tôm, Bò, Chả, Trứng, Bò viên'),
('Bún nước Đặc Biệt', 'MAIN', 'ACTIVE', 0, 50000, 'Bún nước, Tôm, Bò, Chả, Trứng, Bò viên'),

('Mì trộn Tôm Bò', 'MAIN', 'ACTIVE', 0, 35000, 'Mì trộn, Tôm, Bò'),
('Mì trộn Thập Cẩm', 'MAIN', 'ACTIVE', 0, 45000, 'Mì trộn, Tôm, Bò, Chả, Trứng, Bò viên'),
('Mì trộn Đặc Biệt', 'MAIN', 'ACTIVE', 0, 50000, 'Mì trộn, Tôm, Bò, Chả, Trứng, Bò viên');

-- SIDE
INSERT INTO items (name, type, status, sold, price, description) VALUES
('Tôm', 'SIDE', 'ACTIVE', 0, 10000, '1 Viên'),
('Trứng', 'SIDE', 'ACTIVE', 0, 5000, '1 Cái'),
('Chả', 'SIDE', 'ACTIVE', 0, 5000, '2 Viên'),
('Bò Viên', 'SIDE', 'ACTIVE', 0, 5000, '3 Viên'),
('Mì trộn', 'SIDE', 'ACTIVE', 0, 10000, '1 Vắt');

-- DRINK
INSERT INTO items (name, type, status, sold, price, description) VALUES
('Trà đá', 'DRINK', 'ACTIVE', 0, 0, 'Chỉ dùng tại quán');