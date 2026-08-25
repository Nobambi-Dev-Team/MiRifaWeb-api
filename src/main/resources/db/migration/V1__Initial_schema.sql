CREATE TABLE users (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	surname VARCHAR(50) NOT NULL,
	phone_number VARCHAR(13) NOT NULL,
	email VARCHAR(200) NOT NULL UNIQUE,
	password VARCHAR(250) NOT NULL,
	created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role varchar(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_roles_roles FOREIGN KEY (role_id) REFERENCES roles (id),
  CONSTRAINT fk_user_roles_users FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE raffles (

	raffle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(150) NOT NULL,
	description VARCHAR(500),
	number_count INT NOT NULL,
	unit_price DECIMAL(10,2) NOT NULL,
	alias_cbu VARCHAR(100),
	start_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	end_date DATETIME,
	category VARCHAR(50) NOT NULL,
	status VARCHAR(50) NOT NULL,
    image_url VARCHAR(255),
	user_id BIGINT NOT NULL, 

    -- clave foránea
    CONSTRAINT fk_raffles_users FOREIGN KEY (user_id) REFERENCES users(id)

);

CREATE TABLE reservations(

    reservation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
	raffle_id BIGINT NOT NULL,
	number INT NOT NULL,
	status VARCHAR(50) NOT NULL,
	reserved_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
	buyer_name VARCHAR(50) NOT NULL,
	buyer_surname VARCHAR(50) NOT NULL,
	buyer_email VARCHAR(200) NOT NULL,
	buyer_phone VARCHAR(13) NOT NULL,
	
	-- Claves foraneas
    CONSTRAINT fk_reservations_raffles FOREIGN KEY (raffle_id) REFERENCES raffles(raffle_id)
);

CREATE TABLE prizes(

	prize_id BIGINT AUTO_INCREMENT PRIMARY KEY,
	raffle_id BIGINT NOT NULL,
	winning_reservation_id BIGINT,
	description VARCHAR(255) NOT NULL,
	position INT NOT NULL,
	
	-- Claves foraneas
    CONSTRAINT fk_prizes_raffles FOREIGN KEY (raffle_id) REFERENCES raffles(raffle_id),
    CONSTRAINT fk_prizes_reservations FOREIGN KEY (winning_reservation_id) REFERENCES reservations(reservation_id)
);






