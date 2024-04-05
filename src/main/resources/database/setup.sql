-- Schema sopra-2023WS-team01

CREATE TABLE IF NOT EXISTS "user" (
          `idUser` INT NOT NULL AUTO_INCREMENT,
          `name` VARCHAR(45) NOT NULL,
          `password` VARCHAR(45) NOT NULL,
          `email` VARCHAR(45) NOT NULL,
          `coins` INT NULL DEFAULT NULL,
          `profile_pic_path` VARCHAR(100) NULL DEFAULT 'images/profilePics/profilePic.png',
          PRIMARY KEY (`idUser`),
          UNIQUE INDEX `name_UNIQUE` (`name` ASC),
          UNIQUE INDEX `email_UNIQUE` (`email` ASC));



CREATE TABLE IF NOT EXISTS `friends` (
         `id_user_1` INT NOT NULL,
         `id_user_2` INT NOT NULL,
         PRIMARY KEY (`id_user_1`, `id_user_2`),
         CONSTRAINT `fk_user_has_user_user1`
             FOREIGN KEY (`id_user_1`)
                 REFERENCES "user" (`idUser`)
                 ON DELETE CASCADE
                 ON UPDATE CASCADE,
         CONSTRAINT `fk_user_has_user_user2`
             FOREIGN KEY (`id_user_2`)
                 REFERENCES "user" (`idUser`)
                 ON DELETE CASCADE
                 ON UPDATE CASCADE);



CREATE TABLE IF NOT EXISTS `quiz` (
          `idQuiz` INT NOT NULL AUTO_INCREMENT,
          `name` VARCHAR(45) NOT NULL,
          PRIMARY KEY (`idQuiz`));



CREATE TABLE IF NOT EXISTS `highscores` (
        `quiz_idQuiz` INT NOT NULL,
        `user_iduser` INT NOT NULL,
        `highscore` INT NOT NULL,
        PRIMARY KEY (`quiz_idQuiz`, `user_iduser`),
        CONSTRAINT `fk_quiz_has_user_quiz1`
            FOREIGN KEY (`quiz_idQuiz`)
                REFERENCES `quiz` (`idQuiz`)
                ON DELETE CASCADE
                ON UPDATE CASCADE,
        CONSTRAINT `fk_quiz_has_user_user1`
            FOREIGN KEY (`user_iduser`)
                REFERENCES "user" (`idUser`)
                ON DELETE CASCADE
                ON UPDATE CASCADE);



CREATE TABLE IF NOT EXISTS `questions` (
       `quiz_idQuiz` INT NOT NULL,
       `question` VARCHAR(255) NOT NULL,
       `correctAnswer` VARCHAR(255) NULL DEFAULT NULL,
       `wrongAnswer1` VARCHAR(255) NULL DEFAULT NULL,
       `wrongAnswer2` VARCHAR(255) NULL DEFAULT NULL,
       `wrongAnswer3` VARCHAR(255) NULL DEFAULT NULL,
       PRIMARY KEY (`quiz_idQuiz`, `question`),
       CONSTRAINT `fk_questions_quiz1`
           FOREIGN KEY (`quiz_idQuiz`)
               REFERENCES `quiz` (`idQuiz`)
               ON DELETE CASCADE
               ON UPDATE CASCADE);


CREATE TABLE IF NOT EXISTS `joker` (
       `name` varchar(45) NOT NULL,
       `price` int NOT NULL,
       `idjoker` int NOT NULL,
       `description` varchar(100) NOT NULL,
       `joker_pic_path` varchar(100) NOT NULL,
       PRIMARY KEY (`idjoker`)
);

CREATE TABLE IF NOT EXISTS `joker_of_users` (
        `user_id` int NOT NULL,
        `joker_id` int NOT NULL,
        `amount` int NOT NULL,
        PRIMARY KEY (`user_id`,`joker_id`),
        CONSTRAINT `joker_id` FOREIGN KEY (`joker_id`) REFERENCES `joker` (`idjoker`),
        CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES "user" (`idUser`)
            ON DELETE CASCADE
            ON UPDATE CASCADE
);
CREATE TABLE IF NOT EXISTS `messages` (
        `id` int NOT NULL AUTO_INCREMENT,
        `sender_id` int NOT NULL,
        `receiver_id` int NOT NULL,
        `message` text NOT NULL,
        `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (`id`),
        CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES "user" (`idUser`),
        CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES "user" (`idUser`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT INTO "user" (idUser, name, password,email, coins, profile_pic_path)
VALUES
    (1, 'MaxMustermann', 'RescueReady', 'max.mueller@example.com', 100, 'images/profilePics/shark.png'),
    (2, 'HannahSchmidt', 'Sonnenlicht22', 'hannah.schmidt@example.com', 150, 'images/profilePics/bunny.png'),
    (3, 'DavidMeier', 'dMeier123', 'david.meier@example.com', 200, 'images/profilePics/bear.png'),
    (4, 'SophieBecker', 'KaffeeLiebhaber', 'sophie.becker@example.com', 120, 'images/profilePics/cat.png'),
    (5, 'JuliusBraun', 'Br0wnie!', 'julius.braun@example.com', 180, 'images/profilePics/capybara.png'),
    (6, 'LauraWagner', 'T@yl0rL0ve', 'laura.wagner@example.com', 90, 'images/profilePics/crocodile.png'),
    (7, 'ChristianSchulz', 'SchulzC', 'christian.schulz@example.com', 300, 'images/profilePics/dog.png'),
    (8, 'JasminHoffmann', 'J4sm1nH4v', 'jasmin.hoffmann@example.com', 250, 'images/profilePics/lion.png'),
    (9, 'MatthiasLehmann', 'M@ttLehm', 'matthias.lehmann@example.com', 80, 'images/profilePics/man.png'),
    (10, 'EmiliaSchmidt', 'L33Em!ly', 'emilia.schmidt@example.com', 220, 'images/profilePics/prince.png'),
    (11, 'JakobKaiser', 'Kaiser007', 'jakob.kaiser@example.com', 160, 'images/profilePics/racoon.png'),
    (12, 'LenaHermann', 'L3n4H3rm', 'lena.hermann@example.com', 270, 'images/profilePics/snake.png'),
    (13, 'MoritzKeller', 'M0r1tzK', 'moritz.keller@example.com', 140,'images/profilePics/racoon.png'),
    (14, 'LuisaKoch', 'LuisaKoch3z', 'luisa.koch@example.com', 190, 'images/profilePics/prince.png'),
    (15, 'AnnaBauer', 'An3Bau', 'anna.bauer@example.com', 110, 'images/profilePics/man.png'),
    (16, 'FelixHartmann', 'F3lixH4rt', 'felix.hartmann@example.com', 230, 'images/profilePics/capybara.png'),
    (17, 'ClaraSchneider', 'ClaraSchneider!', 'clara.schneider@example.com', 200, 'images/profilePics/crocodile.png'),
    (18, 'JohannesFischer', 'J0hFisch', 'johannes.fischer@example.com', 260, 'images/profilePics/bear.png'),
    (19, 'LeaMeyer', 'L3aMey', 'lea.meyer@example.com', 170, 'images/profilePics/squirrel.png'),
    (20, 'PaulaWeber', 'PaulaWeber#', 'paula.weber@example.com', 210, 'images/profilePics/snake.png');

INSERT INTO quiz VALUES (1, 'Erste Hilfe bei Knochenbrüchen');
INSERT INTO quiz VALUES (2, 'Wiederbelebung (CPR)');
INSERT INTO quiz VALUES (3, 'Verbrennungen');

INSERT INTO questions VALUES (1,'Was ist bei der Ersten Hilfe für einen offenen Bruch zu beachten?' ,'Die Wunde steril verbinden ohne den Bruch zu berühren' ,'Leichter Druck auf die Wunde zur Blutstillung' ,'Hochlagern des verletzten Bereichs zur Schmerzlinderung' ,'Leichtes Kühlen der Wunde zur Vermeidung von Schwellungen' );
INSERT INTO questions VALUES (1,'Welche Maßnahme ist bei Verdacht auf einen Wirbelsäulenbruch sofort zu ergreifen?' ,'Die Person ruhig halten und nicht bewegen' ,'Leichte Neigung des Kopfes zur Atemerleichterung' ,'Vorsichtige Seitenlage zur Stabilisierung' ,'Sanftes Anheben der Beine zur Schockprävention' );
INSERT INTO questions VALUES (1,'Welches ist ein typisches Anzeichen eines Knochenbruchs?' ,'Deutliche Fehlstellung oder unnatürliche Beweglichkeit' ,'Anhaltende Taubheit im verletzten Bereich' ,'Starke Schwellung und Bluterguss' ,'Anhaltender dumpfer Schmerz' );
INSERT INTO questions VALUES (1,'Wie sollte ein gebrochener Arm vor der Ankunft des Rettungsdienstes gelagert werden?' ,'In einer Schlinge, um Bewegung zu minimieren' ,'In einer leicht gebeugten Position zur Schmerzlinderung' ,'Flach und gestützt um die Durchblutung zu fördern' ,'Leichtes Hochhalten zur Verringerung der Schwellung' );
INSERT INTO questions VALUES (1,'Wie sollten Sie reagieren, wenn Sie vermuten, dass jemand einen Knochenbruch hat?' ,'Den Bereich ruhigstellen und den Rettungsdienst alarmieren' ,'Leichter Druck auf die verletzte Stelle, um die Art des Bruchs zu beurteilen' ,'Vorsichtiges Bewegen des Bereichs zur Überprüfung der Beweglichkeit' ,'Anlegen eines festen Verbandes zur Stabilisierung' );
INSERT INTO questions VALUES (2,'In welchem Verhältnis sollten bei Erwachsenen Beatmung und Herzdruckmassage durchgeführt werden?' ,'2 Beatmungen zu 30 Herzdruckmassagen' ,'1 Beatmung zu 5 Herzdruckmassagen' ,'2 Beatmungen zu 15 Herzdruckmassagen' ,'1 Beatmung zu 10 Herzdruckmassagen' );
INSERT INTO questions VALUES (2,'Was ist der erste Schritt in der CPR (kardiopulmonalen Reanimation)?' ,'überprüfen des Bewusstseins' ,'Beatmung' ,'Herzdruckmassage' ,'Alarmieren des Rettungsdienstes' );
INSERT INTO questions VALUES (2,'Was sollten Sie tun, wenn eine Person bewusstlos ist aber noch normal atmet?' ,'Die Person in die stabile Seitenlage bringen' ,'Mit der Herzdruckmassage beginnen' ,'Die Person wecken und beruhigen' ,'Sofort mit Beatmung beginnen' );
INSERT INTO questions VALUES (2,'Welche Maßnahme ist bei einem Erstickungsunfall für eine bewusstlose Person geeignet?' ,'Überprüfung des Mundraums und Entfernung sichtbarer Blockaden vor der CPR' ,'Sofort mit Beatmungen beginnen' ,'Rückenlage und ruhiges Zuwarten auf Besserung' ,'Abdominal-Thrusts (Heimlich-Manöver) durchführen' );
INSERT INTO questions VALUES (2,'Wie oft sollten die Herzdruckmassagen bei einem Erwachsenen wÃ¤hrend der CPR durchgeführt werden?' ,'100-120 Mal pro Minute' ,'60-70 Mal pro Minute' ,'80-90 Mal pro Minute' ,'130-150 Mal pro Minute' );
INSERT INTO questions VALUES (3,'Warum sollte das Entfernen von in die Haut eingebrannten Materialien vermieden werden?' ,'Um eine Infektion zu verhindern und die Haut zu schützen' ,'Um die verbrannte Stelle nicht weiter zu reizen' ,'Um die Schmerzen des Betroffenen nicht zu verstärken' ,'Um die Brandwunde nicht weiter zu öffnen' );
INSERT INTO questions VALUES (3,'Warum sollte das Kühlen von großflächigen Verbrennungen vermieden werden?' ,'Es kann zu einer Unterkühlung des Körpers führen' ,'Es kann die Wundheilung verzögern' ,'Es kann zu Blasenbildung fÃ¼hren' ,'Es kann die Schmerzen verstÃ¤rken' );
INSERT INTO questions VALUES (3,'Was ist ein Zeichen dafür, dass eine Verbrennung möglicherweise schwerwiegender ist?' ,'Keine Schmerzen im verbrannten Bereich' ,'Bildung von Blasen auf der verbrannten Haut' ,'Rötung und Schwellung' ,'Rasche Abkühlung der verbrannten Stelle' );
INSERT INTO questions VALUES (3,'Welche Maßnahme ist bei einer Verbrennung mit heißem Wasser falsch?' ,'Sofort kaltes Wasser über die Verbrennung gießen' ,'Den verbrannten Bereich mit einem sauberen Tuch bedecken' ,'Die verbrannte Stelle mindestens 10 Minuten lang kühlen' ,'Entfernen von Schmuck oder eng anliegender Kleidung über der Verbrennung' );
INSERT INTO questions VALUES (3,'Wie können Verbrennungen durch heiße Flüssigkeiten behandelt werden, wenn sie kleinflächig sind?' ,'Die verbrannte Stelle sofort mit Wasser überspülen' ,'Die Wunde mit einer warmen Kompresse bedecken' ,'Die verbrannte Stelle mit einer Creme einreiben' ,'Die verbrannte Stelle mit Eis kühlen' );

INSERT INTO `highscores` VALUES
                             (1,1,55),
                             (2,1,39),
                             (3,1,22),
                             (1,2,61),
                             (2,2,116),
                             (3,2,84),
                             (1,3,105),
                             (2,3,100),
                             (3,3,54),
                             (1,4,34),
                             (2,4,114),
                             (3,4,101),
                             (1,5,22),
                             (2,5,111),
                             (3,5,84),
                             (1,6,38),
                             (2,6,102),
                             (3,6,98),
                             (1,7,93),
                             (2,7,45),
                             (3,7,24),
                             (1,8,36),
                             (2,8,74),
                             (3,8,51),
                             (1,9,23),
                             (2,9,73),
                             (3,9,69),
                             (1,10,90),
                             (2,10,65),
                             (3,10,99),
                             (1,11,36),
                             (2,11,88),
                             (3,11,40),
                             (1,12,47),
                             (2,12,54),
                             (3,12,71),
                             (1,13,91),
                             (2,13,74),
                             (3,13,93),
                             (1,14,77),
                             (2,14,47),
                             (3,14,100),
                             (1,15,118),
                             (2,15,105),
                             (3,15,70),
                             (1,16,61),
                             (2,16,57),
                             (3,16,24),
                             (1,17,78),
                             (2,17,89),
                             (3,17,79),
                             (1,18,51),
                             (2,18,32),
                             (3,18,34),
                             (1,19,69),
                             (2,19,22),
                             (3,19,35),
                             (1,20,102),
                             (2,20,72),
                             (3,20,78);

INSERT INTO `friends` VALUES
                          (18, 17),
                          (12, 19),
                          (19, 3),
                          (8, 9),
                          (11, 2),
                          (11, 5),
                          (8, 15),
                          (11, 11),
                          (9, 20),
                          (20, 4),
                          (6, 8),
                          (8, 2),
                          (14, 15),
                          (19, 11),
                          (4, 20),
                          (10, 5),
                          (1, 2),
                          (11, 10),
                          (13, 1),
                          (1, 5),
                          (15, 4),
                          (13, 16),
                          (13, 13),
                          (13, 19),
                          (16, 18),
                          (7, 18),
                          (14, 11),
                          (17, 7),
                          (4, 13),
                          (4, 19),
                          (3, 17),
                          (8, 7),
                          (3, 20),
                          (17, 13),
                          (16, 2),
                          (13, 6),
                          (2, 15),
                          (18, 5),
                          (16, 17),
                          (7, 14);

INSERT INTO `joker` VALUES
                        ('Fifty Fifty Joker',5,1,'Entfernt zwei falsche Antworten für die aktuelle Frage','images/fiftyFiftyJoker.png'),
                        ('Pause Joker',10,2,'Pausiert die Zeit für die aktuelle Frage','images/pauseJoker.png'),
                        ('Double Points Joker',20,3,'Verdoppelt die Punkte für die aktuelle Frage','images/doubleItJoker.png');

INSERT INTO `joker_of_users` VALUES
                                 (1,1,1),
                                 (1,2,5),
                                 (1,3,2),
                                 (2,1,4),
                                 (2,2,5),
                                 (2,3,0),
                                 (3,1,0),
                                 (3,2,5),
                                 (3,3,4),
                                 (4,1,1),
                                 (4,2,5),
                                 (4,3,4),
                                 (5,1,1),
                                 (5,2,2),
                                 (5,3,4),
                                 (6,1,1),
                                 (6,2,3),
                                 (6,3,0),
                                 (7,1,2),
                                 (7,2,0),
                                 (7,3,5),
                                 (8,1,1),
                                 (8,2,0),
                                 (8,3,5),
                                 (9,1,5),
                                 (9,2,4),
                                 (9,3,1),
                                 (10,1,4),
                                 (10,2,2),
                                 (10,3,0),
                                 (11,1,3),
                                 (11,2,3),
                                 (11,3,5),
                                 (12,1,5),
                                 (12,2,3),
                                 (12,3,5),
                                 (13,1,4),
                                 (13,2,3),
                                 (13,3,3),
                                 (14,1,4),
                                 (14,2,3),
                                 (14,3,2),
                                 (15,1,1),
                                 (15,2,5),
                                 (15,3,3),
                                 (16,1,3),
                                 (16,2,3),
                                 (16,3,4),
                                 (17,1,3),
                                 (17,2,4),
                                 (17,3,4),
                                 (18,1,5),
                                 (18,2,5),
                                 (18,3,2),
                                 (19,1,2),
                                 (19,2,3),
                                 (19,3,3),
                                 (20,1,2),
                                 (20,2,4),
                                 (20,3,0);