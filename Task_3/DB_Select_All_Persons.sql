CREATE TABLE ukranians AS
    SELECT s.statementid as 'Person_ID', p.fullname, p.type, n.code, a.address as 'Person_Address'
        FROM person_names p
            JOIN person_nationalities n on p._link_person_statement = n._link_person_statement
            JOIN person_addresses a on p._link_person_statement = a._link_person_statement
            JOIN ukranian_surnames sn on p.familyname = sn.surname
            JOIN person_statement s on p._link_person_statement = s._link
                WHERE code <> "UA" AND code <> "RU"
    UNION
    SELECT s.statementid as 'Person_ID', p.fullname, p.type, n.code, a.address as 'Person_Address'
        FROM person_names p
            JOIN person_nationalities n on p._link_person_statement = n._link_person_statement
            JOIN person_addresses a on p._link_person_statement = a._link_person_statement
            JOIN person_statement s on p._link_person_statement = s._link
            WHERE code = "UA";