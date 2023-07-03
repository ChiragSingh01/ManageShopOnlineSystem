create database ManageShopOnline;
use ManageShopOnline;

create table product(
ProId varchar(100) primary key,
ProName varchar(50) not null,
ProDescription varchar(500),
ProPrice int check(ProPrice>0),
Stock int check (Stock>0 or Stock=0)
);

create table Categories(
CateID varchar(100) primary key,
CateName varchar(50) not null,
categoriesProID varchar(100),
CateDescription varchar(5000) not null,
foreign key (categoriesProID) references Product(ProID) on delete cascade on update cascade
);

create table Invoice(
InvoiceID varchar(100) primary key,
invoiceCusID varchar(100),
Invoice_Date datetime,
foreign key (invoiceCusID) references DeliveryDetails(deliverydetailsCusID) on delete cascade on update cascade
);

create table InvoiceProduct(
invoice_productInvoiceID varchar(100),
invoice_productProID varchar(100),
Quantity int check(Quantity>0),
Invoice_Date datetime,
foreign key (invoice_productInvoiceID) references Invoice(InvoiceID) on delete cascade on update cascade,
foreign key (invoice_productProID) references Product(ProID) on delete cascade on update cascade
);

create table Feedback(
feedbackCusID varchar(100),
feedbackProID varchar(100),
comment varchar(300)unique,
FD_Date datetime,
foreign key (feedbackCusID) references Invoice(invoiceCusID) on delete cascade on update cascade,
foreign key (feedbackProID) references Product(ProID) on delete cascade on update cascade
);

create table CustomerAccounts(
CusID varchar(100) primary key,
AccName varchar(30) not null,
AccPass varchar(20) not null,
AccPhone varchar(11)not null,
AccAddress varchar(50) not null,
AccEmail varchar(50) unique,
AC_Status varchar(20) check (AC_Status ='activate' or  AC_Status = 'deactivate')
);

create table DeliveryDetails(
deliverydetailsCusID varchar(100),
CusName varchar(50) not  null,
CusEmail varchar(50) unique,
CusAddress varchar(100)not null,
foreign key (deliverydetailsCusID) references CustomerAccounts(CusID) on delete cascade on update cascade
);

create table Gift(
giftCusID varchar(100),
Message varchar(500),
foreign key (giftCusID) references DeliveryDetails(deliverydetailsCusID) on delete cascade on update cascade
);

drop database manageshoponline;
select * from product;