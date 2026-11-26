CREATE TABLE IF NOT EXISTS trabajadores (
    documento TEXT PRIMARY KEY,
    nombre TEXT NOT NULL,
    fecha_ingreso TEXT NOT NULL,
    tipo TEXT NOT NULL,
    salario_base REAL,
    valor_contrato REAL,
    horas_trabajadas INTEGER,
    valor_hora REAL
);

CREATE TABLE IF NOT EXISTS liquidaciones (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    documento TEXT NOT NULL,
    nombre TEXT NOT NULL,
    fecha_ingreso TEXT NOT NULL,
    tipo TEXT NOT NULL,
    periodo TEXT NOT NULL,
    fecha_liquidacion TEXT NOT NULL,
    salario_base REAL,
    valor_contrato REAL,
    horas_trabajadas INTEGER,
    valor_hora REAL,
    pago REAL NOT NULL,
    bono REAL NOT NULL,
    total REAL NOT NULL,

    FOREIGN KEY (documento)
        REFERENCES trabajadores(documento)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    UNIQUE (documento, periodo)
);