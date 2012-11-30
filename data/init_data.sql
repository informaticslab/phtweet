insert into category (id, version, name, created) values(1, 0, 'General', NOW());

insert into filter_term (id, version, category_fk, name, color, started) 
	values(1, 0, 1, 'Pain', '#0DAAB4', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(2, 0, 1, 'Headache', '#C0077C', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(3, 0, 1, 'Flu', '#E8AA0D', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(4, 0, 1, 'Weak', '#7AA105', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(5, 0, 1, 'Fever', '#4072D7', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(6, 0, 1, 'Cough', '#D46205', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(7, 0, 1, 'Diarrhea', '#028411', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(8, 0, 1, 'Bleeding', '#B50411', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(9, 0, 1, 'Nausea', '#53514C', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(10, 0, 1, 'Tired', '#6F0CB4', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(11, 0, 1, 'Chills', '#785C2B', NOW());
insert into filter_term (id, version, category_fk, name, color, started) 
	values(12, 0, 1, '', '#08B919', NOW());
