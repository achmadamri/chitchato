-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 31 Mar 2024 pada 09.40
-- Versi server: 10.4.27-MariaDB
-- Versi PHP: 8.0.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `chitchato_backend`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `config`
--

CREATE TABLE `config` (
  `key` varchar(100) DEFAULT NULL,
  `value` varchar(5000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `config`
--

INSERT INTO `config` (`key`, `value`) VALUES
('system_prompt', 'You are the persona of a Customer Service Assistant, engaging in casual conversation. Your primary objective is to interact with users in a manner that not only addresses their immediate queries or concerns but also subtly gauges their interest in becoming prospective customers. Your responses should be informative, friendly, and tailored to encourage users to see the value in our services, nudging them towards considering a purchase or subscription. Listen attentively to their needs, provide solutions, and highlight how our offerings can specifically benefit them, turning their interest into potential sales opportunities.'),
('task_prompt', 'Carefully review the provided documents to identify any sections that could assist the user in resolving their issue. Once identified, clearly explain the relevance of these sections and how they can be effectively applied to the user\'s specific situation. Your explanation should be detailed, yet easily understandable, ensuring the user feels fully supported. If the documents do not contain any relevant information, craft a response that maintains a positive and supportive tone. Assure the user that their concern is valid and important, and provide alternative solutions or suggest next steps, if possible. Your goal is to uphold a positive user experience, ensuring the user feels heard, supported, and valued, regardless of the document\'s contents.');

-- --------------------------------------------------------

--
-- Struktur dari tabel `connector`
--

CREATE TABLE `connector` (
  `uuid` varchar(100) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `file_names` varchar(100) DEFAULT NULL,
  `connector_id` int(11) DEFAULT NULL,
  `cc_pair_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `connector`
--

INSERT INTO `connector` (`uuid`, `created_at`, `created_by`, `update_at`, `updated_by`, `file_names`, `connector_id`, `cc_pair_id`) VALUES
('a80885e8-a06d-49cd-97c9-ae563ed18d3f', '2024-03-29 00:00:55', 'achmad.amri@gmail.com', NULL, NULL, 'Company Profile AA 2020 (light).pdf', 75, 74),
('b2c0e97b-d5e9-4bc3-9cf6-e082061ba0e7', '2024-03-29 00:01:03', 'achmad.amri@gmail.com', NULL, NULL, 'Company Profile AA 2020 (light).pdf', 76, 75),
('8e76b4f7-c63f-4bf5-abf9-98c4489d19c8', '2024-03-29 00:02:08', 'achmad.amri@gmail.com', NULL, NULL, 'Company Profile AA 2020 (light).pdf', 77, 76),
('d1ffb334-b640-4673-888f-db6449d4d2b8', '2024-03-29 00:03:42', 'achmad.amri@gmail.com', NULL, NULL, 'Company Profile AA 2020 (light).pdf', 78, 77);

-- --------------------------------------------------------

--
-- Struktur dari tabel `document_set`
--

CREATE TABLE `document_set` (
  `uuid` varchar(100) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `document_set_id` int(11) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `document_set`
--

INSERT INTO `document_set` (`uuid`, `created_at`, `created_by`, `update_at`, `updated_by`, `document_set_id`, `name`, `description`) VALUES
('a80885e8-a06d-49cd-97c9-ae563ed18d3f', '2024-03-29 00:00:55', 'achmad.amri@gmail.com', NULL, NULL, 40, 'a80885e8-a06d-49cd-97c9-ae563ed18d3f', 'a80885e8-a06d-49cd-97c9-ae563ed18d3f');

-- --------------------------------------------------------

--
-- Struktur dari tabel `persona`
--

CREATE TABLE `persona` (
  `uuid` varchar(100) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `persona_id` int(11) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `prompt_id` int(11) DEFAULT NULL,
  `document_set_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `persona`
--

INSERT INTO `persona` (`uuid`, `created_at`, `created_by`, `update_at`, `updated_by`, `persona_id`, `name`, `description`, `prompt_id`, `document_set_id`) VALUES
('a80885e8-a06d-49cd-97c9-ae563ed18d3f', '2024-03-29 00:00:55', 'achmad.amri@gmail.com', NULL, NULL, 27, 'a80885e8-a06d-49cd-97c9-ae563ed18d3f', 'a80885e8-a06d-49cd-97c9-ae563ed18d3f', 31, 40);

-- --------------------------------------------------------

--
-- Struktur dari tabel `prompt`
--

CREATE TABLE `prompt` (
  `uuid` varchar(100) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(100) DEFAULT NULL,
  `update_at` datetime DEFAULT NULL,
  `updated_by` varchar(100) DEFAULT NULL,
  `prompt_id` int(11) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `system_prompt` varchar(2000) DEFAULT NULL,
  `task_prompt` varchar(2000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `prompt`
--

INSERT INTO `prompt` (`uuid`, `created_at`, `created_by`, `update_at`, `updated_by`, `prompt_id`, `name`, `description`, `system_prompt`, `task_prompt`) VALUES
('a80885e8-a06d-49cd-97c9-ae563ed18d3f', '2024-03-29 00:00:55', 'achmad.amri@gmail.com', NULL, NULL, 31, 'default-prompt__a80885e8-a06d-49cd-97c9-ae563ed18d3f', 'Default prompt for persona a80885e8-a06d-49cd-97c9-ae563ed18d3f', 'You are the persona of a Customer Service Assistant, engaging in casual conversation. Your primary objective is to interact with users in a manner that not only addresses their immediate queries or concerns but also subtly gauges their interest in becoming prospective customers. Your responses should be informative, friendly, and tailored to encourage users to see the value in our services, nudging them towards considering a purchase or subscription. Listen attentively to their needs, provide solutions, and highlight how our offerings can specifically benefit them, turning their interest into potential sales opportunities.', 'Carefully review the provided documents to identify any sections that could assist the user in resolving their issue. Once identified, clearly explain the relevance of these sections and how they can be effectively applied to the user\'s specific situation. Your explanation should be detailed, yet easily understandable, ensuring the user feels fully supported. If the documents do not contain any relevant information, craft a response that maintains a positive and supportive tone. Assure the user that their concern is valid and important, and provide alternative solutions or suggest next steps, if possible. Your goal is to uphold a positive user experience, ensuring the user feels heard, supported, and valued, regardless of the document\'s contents.');

-- --------------------------------------------------------

--
-- Struktur dari tabel `user`
--

CREATE TABLE `user` (
  `username` varchar(255) DEFAULT NULL,
  `username_danswer` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `user`
--

INSERT INTO `user` (`username`, `username_danswer`) VALUES
('achmad.amri@gmail.com', 'administrator@chitchato.com');

-- --------------------------------------------------------

--
-- Struktur dari tabel `user_danswer`
--

CREATE TABLE `user_danswer` (
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `user_danswer`
--

INSERT INTO `user_danswer` (`username`, `password`) VALUES
('administrator@chitchato.com', '2mC3£9x3[9Nm');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
