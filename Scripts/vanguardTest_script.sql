CREATE TABLE game_sales_db.csv_import_progress (
    id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(100),
    file_data LONGBLOB,
    total_records INT,
    start_time DATETIME,
    end_time DATETIME,
    import_status VARCHAR(20),
    error_log LONGTEXT
);

CREATE TABLE game_sales_db.game_sales (
    id INT NOT NULL,
    csv_id INT NOT NULL,
    game_no INT,
    game_name VARCHAR(20),
    game_code VARCHAR(5),
    type INT,
    cost_price DOUBLE,
    tax INT,
    sale_price DOUBLE,
    date_of_sale DATE,
    PRIMARY KEY (id, csv_id)
); 

CREATE TABLE game_sales_db.sale_summary (
    id INT AUTO_INCREMENT PRIMARY KEY,
	game_no INT NOT NULL,
	game_name VARCHAR(20) NOT NULL,
    date_of_sale DATETIME NOT NULL,
    total_no_of_game_sold INT NOT NULL,
    sale_price DECIMAL(10, 2) NOT NULL,
	UNIQUE KEY unique_game_date (game_no, date_of_sale) 
);

SET GLOBAL max_allowed_packet=268435456;
