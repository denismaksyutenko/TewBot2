CREATE TABLE words_and_translate
(
    id              BIGINT NOT NULL,
    english_words   VARCHAR(255),
    ukrainian_words VARCHAR(255),
    CONSTRAINT pk_wordsandtranslate PRIMARY KEY (id)
);
--INSERT INTO 'WORDS_AND_TRANSLATE' ('ENGLISH_WORDS','UKRAINIAN_WORDS') values ('hello', 'привіт' );
--INSERT INTO 'WORDS_AND_TRANSLATE' ('ENGLISH_WORDS','UKRAINIAN_WORDS') values ('world', 'світ' );