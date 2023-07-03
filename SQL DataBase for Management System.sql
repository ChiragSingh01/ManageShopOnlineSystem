create database ManageShopOnline;
use ManageShopOnline;

create table product(
ProId varchar(10) primary key,
ProName varchar(50) not null,
ProDescription varchar(500),
ProPrice int check(ProPrice>0),
Stock int check (Stock>0 or Stock=0)
);

create table Categories(
CateID varchar(10) primary key,
CateName varchar(50) not null,
categoriesProID varchar(10),
CateDescription varchar(500) not null,
foreign key (categoriesProID) references Product(ProID) on delete cascade on update cascade
);

create table Invoice(
InvoiceID varchar(10) primary key,
invoiceCusID varchar(10),
Invoice_Date datetime,
foreign key (invoiceCusID) references DeliveryDetails(deliverydetailsCusID) on delete cascade on update cascade
);

create table InvoiceProduct(
invoice_productInvoiceID varchar(10),
invoice_productProID varchar(10),
Quantity int check(Quantity>0),
Invoice_Date datetime,
foreign key (invoice_productInvoiceID) references Invoice(InvoiceID) on delete cascade on update cascade,
foreign key (invoice_productProID) references Product(ProID) on delete cascade on update cascade
);

create table Feedback(
feedbackCusID varchar(10),
feedbackProID varchar(10),
comment varchar(300)unique,
FD_Date datetime,
foreign key (feedbackCusID) references Invoice(invoiceCusID) on delete cascade on update cascade,
foreign key (feedbackProID) references Product(ProID) on delete cascade on update cascade
);

create table ManageAccount(
CusID varchar(50) primary key,
AccName varchar(30) not null,
AccPass varchar(20) not null,
AccPhone varchar(11)not null,
AccAddress varchar(50) not null,
AccEmail varchar(50) unique,
AC_Status varchar(20) check (AC_Status ='activate' or  AC_Status = 'deactivate')
);

create table DeliveryDetails(
deliverydetailsCusID varchar(10),
CusName varchar(50) not  null,
CusEmail varchar(50) unique,
CusAddress varchar(100)not null,
foreign key (deliverydetailsCusID) references ManageAccount(CusID) on delete cascade on update cascade
);

create table Gift(
giftCusID varchar(10),
Message varchar(500),
foreign key (giftCusID) references DeliveryDetails(deliverydetailsCusID) on delete cascade on update cascade
);