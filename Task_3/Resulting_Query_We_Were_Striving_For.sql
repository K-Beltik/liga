SELECT  ua.Person_ID,  ua.fullname, ua.type as 'Citizenship', ua.code, ua.Person_Address,
        es.statementid as 'Business_ID', es.name, es.source_description, es.entitytype, es.foundingdate, ea.address as 'Business_Address'
FROM ukranians ua
    JOIN ooc_statement os on ua.Person_ID = os.interestedparty_describedbypersonstatement
    JOIN entity_statement es on os.subject_describedbyentitystatement = es.statementid
    JOIN entity_addresses ea on es._link = ea._link_entity_statement;