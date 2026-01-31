-- Schema migration to support reservations
-- Ensure products table has reserved_quantity column for SQL Server
BEGIN TRY
    IF NOT EXISTS (
        SELECT 1 FROM sys.columns 
        WHERE Name = N'reserved_quantity' AND Object_ID = Object_ID(N'products')
    )
    BEGIN
        ALTER TABLE products ADD reserved_quantity INT NOT NULL DEFAULT 0;
    END
END TRY
BEGIN CATCH
    -- Swallow errors to avoid startup failure
    PRINT 'Schema init warning: ' + ERROR_MESSAGE();
END CATCH
