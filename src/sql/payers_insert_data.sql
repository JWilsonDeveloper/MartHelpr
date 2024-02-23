INSERT INTO payers (payer_name, created)
SELECT 'Deleted', null
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM payers);
