-- Encode ULID from string
DROP ALIAS IF EXISTS ULID_ENCODE;
CREATE ALIAS ULID_ENCODE FOR "com.pcagrade.mason.test.ulid.TestUlidHelper.encode";

-- Decode ULID to string
DROP ALIAS IF EXISTS ULID_DECODE;
CREATE ALIAS ULID_DECODE FOR "com.pcagrade.mason.test.ulid.TestUlidHelper.decode";

-- Create random ULID
DROP ALIAS IF EXISTS ULID_CREATE;
CREATE ALIAS ULID_CREATE FOR "com.pcagrade.mason.test.ulid.TestUlidHelper.create";
