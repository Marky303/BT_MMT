# Cac library can thiet
library("tidyverse")
library("plyr")
library("moments")
library("csv")
library("csvread")
library("readr")
library("rlist")
library("lubridate")
library("ggplot2")
library("dplyr")
library("plotly")
library("cowplot")
library("caret")
library("vcd")
library("ResourceSelection")
library("pROC")
library("corrplot")
library(moments)
library("car")
library("caTools")
library("nortest")
library("ggpubr")
rm(list=ls())

# Doc file du lieu_____________________________________________________________________________________________________
setwd("C:/Users/FPTSHOP/Desktop/BTLxxtk")         # Nhap dia chi cua thu muc
file<-read_csv("ALL_GPUs.csv")                    # Doc du lieu tu file csv

# Thong ke cac gia tri null trong file (tat ca cac bien)_______________________________________________________________
nullValues<-colSums(is.na(file))
varCount = ncol(file)
obsCount = nrow(file)
for (i in 1:varCount)
{
  if (colnames(file[i])=="Core_Speed")
  {
    nullValues[i] = 100*((nullValues[i]+sum(file$Core_Speed=="\n-")+sum(file$Core_Speed=="\n"))/obsCount) 
  }
  else if (colnames(file[i])=="Release_Date")
  {
    nullValues[i] = 100*((nullValues[i]+sum(file$Release_Date=="\nUnknown Release Date"))/obsCount)
  }
  else 
  {
    nullValues[i] = 100*(nullValues[i]/obsCount) 
  }
}
dev.new(width=12,height=7)
par(mai=c(2,1,1,1))
midpoints<-barplot(nullValues,width=rep(c(10),each=varCount),space=0.5,las=2,main="Overall null value statistic 1.0")
text(x=midpoints,2,labels=round(nullValues,0))

# Chon cac cot can thiet va thong ke null value________________________________________________________________________
data<-select(file,Core_Speed,Max_Power,Memory,Memory_Bandwidth,Memory_Bus,Memory_Speed,Pixel_Rate,Process,Release_Date)
nullValues<-colSums(is.na(data))
varCount = ncol(data)
obsCount = nrow(data)
for (i in 1:varCount)
{
  if (colnames(data[i])=="Core_Speed")
  {
    nullValues[i] = 100*((nullValues[i]+sum(data$Core_Speed=="\n-")+sum(file$Core_Speed=="\n"))/obsCount) 
  }
  else if (colnames(data[i])=="Release_Date")
  {
    nullValues[i] = 100*((nullValues[i]+sum(data$Release_Date=="\nUnknown Release Date"))/obsCount)
  }
  else 
  {
    nullValues[i] = 100*(nullValues[i]/obsCount) 
  }
}
dev.new(width=12,height=7)
par(mai=c(2,1,1,0.3))
midpoints<-barplot(nullValues,width=rep(c(10),each=varCount),space=0.5,las=2,main="Null value statistics 1.1")
text(x=midpoints,2,labels=round(nullValues,3))

# Xu lu kieu du lieu
data$Core_Speed<-gsub(" MHz","",data$Core_Speed)
data$Core_Speed<-strtoi(data$Core_Speed)

data$Max_Power<-gsub(" Watts","",data$Max_Power)
data$Max_Power<-strtoi(data$Max_Power)

data$Memory<-gsub(" MB","",data$Memory)
data$Memory<-strtoi(data$Memory)

for (i in 1:nrow(data))
{
  if (grepl("GB/sec",data$Memory_Bandwidth[i]))
  {
    data$Memory_Bandwidth[i]<-gsub("GB/sec","",data$Memory_Bandwidth[i])
    data$Memory_Bandwidth[i]<-as.double(data$Memory_Bandwidth[i])
  }
  else if (grepl("MB/sec",data$Memory_Bandwidth[i]))
  {
    data$Memory_Bandwidth[i]<-gsub("MB/sec","",data$Memory_Bandwidth[i])
    data$Memory_Bandwidth[i]<-as.double(data$Memory_Bandwidth[i])/1024
  }
}
data$Memory_Bandwidth<-as.double(data$Memory_Bandwidth)

data$Memory_Bus<-gsub(" Bit","",data$Memory_Bus)
data$Memory_Bus<-strtoi(data$Memory_Bus)

data$Memory_Speed<-gsub(" MHz","",data$Memory_Speed)
data$Memory_Speed<-strtoi(data$Memory_Speed)

data$Pixel_Rate<-gsub(" GPixel/s","",data$Pixel_Rate)
data$Pixel_Rate<-strtoi(data$Pixel_Rate)

data$Process<-gsub("nm","",data$Process)
data$Process<-strtoi(data$Process)

data$Release_Date<-gsub("Jan","01",data$Release_Date)
data$Release_Date<-gsub("Feb","02",data$Release_Date)
data$Release_Date<-gsub("Mar","03",data$Release_Date)
data$Release_Date<-gsub("Apr","04",data$Release_Date)
data$Release_Date<-gsub("May","05",data$Release_Date)
data$Release_Date<-gsub("Jun","06",data$Release_Date)
data$Release_Date<-gsub("Jul","07",data$Release_Date)
data$Release_Date<-gsub("Aug","08",data$Release_Date)
data$Release_Date<-gsub("Sep","09",data$Release_Date)
data$Release_Date<-gsub("Oct","10",data$Release_Date)
data$Release_Date<-gsub("Nov","11",data$Release_Date)
data$Release_Date<-gsub("Dec","12",data$Release_Date)
data$Release_Date<-gsub("\n","",data$Release_Date)
data$Release_Date<-as.Date(data$Release_Date,"%d-%m-%Y")
minReleaseDate<-min(data$Release_Date[!is.na(data$Release_Date)])
data$Release_Date<-data$Release_Date-minReleaseDate
data$Release_Date<-as.numeric(data$Release_Date)
rm(minReleaseDate)

# Xu ly outlier________________________________________________________________________________________________________
# Thong ke outlier
outlier_perc<-numeric()
# Bien Core_Speed
CoreSpeed<-data$Core_Speed
CoreSpeed<-na.omit(CoreSpeed)
quartiles <- quantile(CoreSpeed, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(CoreSpeed)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
CoreSpeed_Sorted<-subset(CoreSpeed, CoreSpeed > rLower & CoreSpeed < rUpper)
outlier_perc<-append(outlier_perc,(length(CoreSpeed)-length(CoreSpeed_Sorted))/length(CoreSpeed)) 
rm(CoreSpeed,CoreSpeed_Sorted)

# Bien Max_power
MaxPower<-data$Max_Power
MaxPower<-na.omit(MaxPower)
quartiles <- quantile(MaxPower, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(MaxPower)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
MaxPower_Sorted<-subset(MaxPower, MaxPower > rLower & MaxPower < rUpper)
outlier_perc<-append(outlier_perc,(length(MaxPower)-length(MaxPower_Sorted))/length(MaxPower)) 
rm(MaxPower,MaxPower_Sorted)

# Bien Memory
Memory_<-data$Memory
Memory_<-na.omit(Memory_)
quartiles <- quantile(Memory_, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(Memory_)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
Memory_Sorted<-subset(Memory_, Memory_ > rLower & Memory_ < rUpper)
outlier_perc<-append(outlier_perc,(length(Memory_)-length(Memory_Sorted))/length(Memory_)) 
rm(Memory_,Memory_Sorted)

# Bien Memory_Bandwidth
MemoryBandwidth<-data$Memory_Bandwidth
MemoryBandwidth<-na.omit(MemoryBandwidth)
quartiles <- quantile(MemoryBandwidth, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(MemoryBandwidth)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
MemoryBandwidth_Sorted<-subset(MemoryBandwidth, MemoryBandwidth > rLower & MemoryBandwidth < rUpper)
outlier_perc<-append(outlier_perc,(length(MemoryBandwidth)-length(MemoryBandwidth_Sorted))/length(MemoryBandwidth)) 
rm(MemoryBandwidth,MemoryBandwidth_Sorted)

# Bien Memory_Bus
MemoryBus<-data$Memory_Bus
MemoryBus<-na.omit(MemoryBus)
quartiles <- quantile(MemoryBus, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(MemoryBus)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
MemoryBus_Sorted<-subset(MemoryBus, MemoryBus > rLower & MemoryBus < rUpper)
outlier_perc<-append(outlier_perc,(length(MemoryBus)-length(MemoryBus_Sorted))/length(MemoryBus)) 
rm(MemoryBus,MemoryBus_Sorted)

# Bien Memory_Speed
MemorySpeed<-data$Memory_Speed
MemorySpeed<-na.omit(MemorySpeed)
quartiles <- quantile(MemorySpeed, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(MemorySpeed)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
MemorySpeed_Sorted<-subset(MemorySpeed, MemorySpeed > rLower & MemorySpeed < rUpper)
outlier_perc<-append(outlier_perc,(length(MemorySpeed)-length(MemorySpeed_Sorted))/length(MemorySpeed)) 
rm(MemorySpeed,MemorySpeed_Sorted)

# Bien Pixel_Rate
PixelRate<-data$Pixel_Rate
PixelRate<-na.omit(PixelRate)
quartiles <- quantile(PixelRate, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(PixelRate)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
PixelRate_Sorted<-subset(PixelRate, PixelRate > rLower & PixelRate < rUpper)
outlier_perc<-append(outlier_perc,(length(PixelRate)-length(PixelRate_Sorted))/length(PixelRate)) 
rm(PixelRate,PixelRate_Sorted)

# Bien Process
Process_<-data$Process
Process_<-na.omit(Process_)
quartiles <- quantile(Process_, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(Process_)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
Process_Sorted<-subset(Process_, Process_ > rLower & Process_ < rUpper)
outlier_perc<-append(outlier_perc,(length(Process_)-length(Process_Sorted))/length(Process_)) 
rm(Process_,Process_Sorted)

# Bien Release_Date
outlier_perc<-append(outlier_perc,0)

# Ve bieu do thong ke outlier
names(outlier_perc)<-labels(nullValues)
outlier_perc<-outlier_perc*100
dev.new(width=12,height=7)
par(mai=c(2,1,1,0.3))
midpoints<-barplot(outlier_perc,width=rep(c(10),each=varCount),space=0.5,las=2,main="Outlier statistics")
text(x=midpoints,0.7,labels=round(outlier_perc,3))

# Xu ly cac outlier dua tren thong ke
# Bien Core_Speed
CoreSpeed<-data$Core_Speed
CoreSpeed<-na.omit(CoreSpeed)
quartiles <- quantile(CoreSpeed, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(CoreSpeed)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
for (i in 1:nrow(data))
{
  if (is.na(data$Core_Speed[i]))
  {
    next
  }
  if (data$Core_Speed[i]>rUpper)
  {
    data$Core_Speed[i] = rUpper
  }
  else if (data$Core_Speed[i]<rLower)
  {
    data$Core_Speed[i] = rLower
  }
}
rm(CoreSpeed)

# Bien Max_power
MaxPower<-data$Max_Power
MaxPower<-na.omit(MaxPower)
quartiles <- quantile(MaxPower, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(MaxPower)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
data<-subset(data,data$Max_Power>rLower&data$Max_Power<rUpper|is.na(data$Max_Power))
rm(MaxPower)

# Bien Memory
Memory_<-data$Memory
Memory_<-na.omit(Memory_)
quartiles <- quantile(Memory_, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(Memory_)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
data<-subset(data,data$Memory>rLower&data$Memory<rUpper|is.na(data$Memory))
rm(Memory_)

# Bien Memory_Bandwidth
MemoryBandwidth<-data$Memory_Bandwidth
MemoryBandwidth<-na.omit(MemoryBandwidth)
quartiles <- quantile(MemoryBandwidth, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(MemoryBandwidth)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
data<-subset(data,data$Memory_Bandwidth>rLower&data$Memory_Bandwidth<rUpper|is.na(data$Memory_Bandwidth))
rm(MemoryBandwidth)

# Bien Memory_Bus
MemoryBus<-data$Memory_Bus
MemoryBus<-na.omit(MemoryBus)
quartiles <- quantile(MemoryBus, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(MemoryBus)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
data<-subset(data,data$Memory_Bus>rLower&data$Memory_Bus<rUpper|is.na(data$Memory_Bus))
rm(MemoryBus)

# Bien Memory_Speed
MemorySpeed<-data$Memory_Speed
MemorySpeed<-na.omit(MemorySpeed)
quartiles <- quantile(MemorySpeed, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(MemorySpeed)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
data<-subset(data,data$Memory_Speed>rLower&data$Memory_Speed<rUpper|is.na(data$Memory_Speed))
rm(MemorySpeed)

# Bien Pixel_Rate
PixelRate<-data$Pixel_Rate
PixelRate<-na.omit(PixelRate)
quartiles <- quantile(PixelRate, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(PixelRate)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
for (i in 1:nrow(data))
{
  if (is.na(data$Pixel_Rate[i]))
  {
    next
  }
  if (data$Pixel_Rate[i]>rUpper)
  {
    data$Pixel_Rate[i] = rUpper
  }
  else if (data$Pixel_Rate[i]<rLower)
  {
    data$Pixel_Rate[i] = rLower
  }
}
rm(PixelRate)

# Bien Process
Process_<-data$Process
Process_<-na.omit(Process_)
quartiles <- quantile(Process_, probs=c(.25, .75), na.rm = FALSE)
IQR <- IQR(Process_)
rLower <- quartiles[1] - 1.5*IQR
rUpper <- quartiles[2] + 1.5*IQR 
data<-subset(data,data$Process>rLower&data$Process<rUpper|is.na(data$Process))
rm(Process_)

rm(rUpper,rLower,quartiles,IQR)

# Xu ly cac gia tri null__________________________________________________________________________________________________
# Thong ke cac gia tri null sau khi xu ly cac outlier
nullValues<-colSums(is.na(data))
varCount = ncol(data)
obsCount = nrow(data)
for (i in 1:varCount)
{
  nullValues[i] = 100*(nullValues[i]/obsCount) 
}
dev.new(width=12,height=7)
par(mai=c(2,1,1,0.3))
midpoints<-barplot(nullValues,width=rep(c(10),each=varCount),space=0.5,las=2,main="Null value statistics 2.0 (After dealing with outliers)")
text(x=midpoints,2,labels=round(nullValues,3))

# Xoa cac gia tri null co the xoa (duoi 5%)
data<-data[!is.na(data$Memory_Bandwidth),]
data<-data[!is.na(data$Memory_Bus),]
data<-data[!is.na(data$Memory_Speed),]
data<-data[!is.na(data$Release_Date),]
# Thong ke gia tri null sau khi xoa cac cot co gia tri null duoi 5%
nullValues<-colSums(is.na(data))
varCount = ncol(data)
obsCount = nrow(data)
for (i in 1:varCount)
{
  nullValues[i] = 100*(nullValues[i]/obsCount) 
}
dev.new(width=12,height=7)
par(mai=c(2,1,1,0.3))
midpoints<-barplot(nullValues,width=rep(c(10),each=varCount),space=0.5,las=2,main="Null value statistics 2.1 (After deleting marginal null values)")
text(x=midpoints,2,labels=round(nullValues,3))

# Thong ke gia tri cua cac bien (xac dinh giap phap giai quyet gia tri null)
# Bien Core_Speed
CoreSpeed<-data$Core_Speed
CoreSpeed<-na.omit(CoreSpeed)
d<-density(CoreSpeed)
dev.new(width=10,height=10)
plot(d)
skewness(CoreSpeed)
rm(CoreSpeed)

#Bien Max_Power
MaxPower<-data$Max_Power
MaxPower<-na.omit(MaxPower)
d<-density(MaxPower)
dev.new(width=10,height=10)
plot(d)
skewness(MaxPower)
rm(MaxPower)

#Bien Memory
Memory_<-data$Memory
Memory_<-na.omit(Memory_)
d<-density(Memory_)
dev.new(width=10,height=10)
plot(d)
skewness(Memory_)
rm(Memory_)

#Bien Pixel_Rate
PixelRate<-data$Pixel_Rate
PixelRate<-na.omit(PixelRate)
d<-density(PixelRate)
dev.new(width=10,height=10)
plot(d)
skewness(PixelRate)
rm(PixelRate)

# Bien Process
Process_<-data$Process
Process_<-na.omit(Process_)
d<-density(Process_)
dev.new(width=10,height=10)
plot(d)
skewness(Process_)
rm(Process_)



# Thay cac gia tri null bang mean hoac median hop ly
# Bien Core_Speed
mean_tmp<-mean(data$Core_Speed,na.rm=TRUE)
for (i in 1:nrow(data))
{
  if (is.na(data$Core_Speed[i]))
  {
    data$Core_Speed[i] = mean_tmp
  }
}

# Bien Max_Power
mean_tmp<-mean(data$Max_Power,na.rm=TRUE)
for (i in 1:nrow(data))
{
  if (is.na(data$Max_Power[i]))
  {
    data$Max_Power[i] = mean_tmp
  }
}

# Bien Memory
med_tmp<-median(data$Memory,na.rm=TRUE)
for (i in 1:nrow(data))
{
  if (is.na(data$Memory[i]))
  {
    data$Memory[i] = med_tmp
  }
}

# Bien Pixel_Rate
med_tmp<-median(data$Pixel_Rate,na.rm=TRUE)
for (i in 1:nrow(data))
{
  if (is.na(data$Pixel_Rate[i]))
  {
    data$Pixel_Rate[i] = med_tmp
  }
}

# Bien Process
mean_tmp<-mean(data$Process,na.rm=TRUE)
for (i in 1:nrow(data))
{
  if (is.na(data$Process[i]))
  {
    data$Process[i] = mean_tmp
  }
}

rm(mean_tmp,med_tmp,i,varCount,obsCount,outlier_perc)

sum(is.na(data))

# Thong ke ta__________________________________________________________________________________________________________

# Tong quat data 
summary(data)

# Core_Speed
dev.new()
hist(data$Core_Speed,
     main = paste("Histogram of Core_Speed"),
     xlab = "Core_speed")
boxplot(data$Core_Speed,
        main = paste("Boxplot of Core_Speed"),
        horizontal = T,
        xlab = "Core_Speed")

# Max_Power
dev.new()
hist(data$Max_Power,
     main = paste("Histogram of Max_Power"),
     xlab = "Max_Power")
boxplot(data$Max_Power,
        main = paste("Boxplot of Max_Power"),
        horizontal = T,
        xlab = "Max_Power")

# Memory
dev.new()
hist(data$Memory,
     main = paste("Histogram of Memory"),
     xlab = "Memory")
boxplot(data$Memory,
        main = paste("Boxplot of Memory"),
        horizontal = T,
        xlab = "Memory"
        )

# Memory_Bandwidth
dev.new()
hist(data$Memory_Bandwidth,
     main = paste("Histogram of Memory_Bandwidth"),
     xlab = "Memory_Bandwidth")
boxplot(data$Memory_Bandwidth,
        main = paste("Boxplot of Memory_Bandwidth"),
        horizontal = T,
        xlab = "Memory_Bandwidth"
        )

# Memory_Bus
dev.new()
hist(data$Memory_Bus,
     main = paste("Histogram of Memory_Bus"),
     xlab = "Memory_Bus")
boxplot(data$Memory_Bus,
        main = paste("Boxplot of Memory_Bus"),
        horizontal = T,
        xlab = "Memory_Bus")

# Memory_Speed
dev.new()
hist(data$Memory_Speed,
     main = paste("Histogram of Memory_Speed"),
     xlab = "Memory_Speed")
boxplot(data$Memory_Speed,
        main = paste("Boxplot of Memory_Speed"),
        horizontal = T,
        xlab = "Memory_Speed")

# Process
dev.new()
hist(data$Process,
     main = paste("Histogram of Process"),
     xlab = "Process")
boxplot(data$Process,
        main = paste("Boxplot of Process"),
        horizontal = T,
        xlab = "Process")

# Release_Date
dev.new()
hist(data$Release_Date,
     main = paste("Histogram of Release_Date"),
     xlab = "Release_Date")
boxplot(data$Release_Date,
        main = paste("Boxplot of Release_Date"),
        horizontal = T,
        xlab = "Release_Date")

# Pixel_Rate
dev.new()
hist(data$Pixel_Rate,
     main = paste("Histogram of Pixel_Rate"),
     xlab = "Pixel_Rate")
boxplot(data$Pixel_Rate,
        main = paste("Boxplot of Pixel_Rate"),
        horizontal = T,
        xlab = "Pixel_Rate")

# Ve ma tran correlation 
dev.new()
corrplot(cor(data),method = 'pie',addCoef.col='red',order='AOE')

# Thong ke suy dien____________________________________________________________________________________________________
model<-lm(Memory_Bandwidth~Core_Speed+Max_Power+Memory+Memory_Bus+Pixel_Rate+Process+Release_Date+Memory_Speed,data=data)
# Assumption check
# Residual normailty check
modelResiduals<-residuals(model)

dev.new(width=10,height=10)
ggqqplot(modelResiduals, main='Residuals')

modelR<-data.frame(modelResiduals)
dev.new(width=10,height=10)
hist.residual = ggplot(data = modelR,aes(modelResiduals))+
  geom_histogram(aes(y=..density..),color="black",fill="white")+
  stat_function(fun = dnorm,
                args = list(mean = mean(modelR$modelResiduals,na.rm = TRUE),
                            sd = sd(modelR$modelResiduals,na.rm = TRUE)),
                color ='blue',size = 1)
hist.residual
rm(hist.residual,modelR,modelResiduals,d,midpoints)

# Linearity
summary(model)

# Multicollinearity
vif(model)

# Performing MLR
# Splitting
set.seed(42)
sample <- sample(c(TRUE, FALSE), nrow(data), replace=TRUE, prob=c(0.8,0.2))
train  <- data[sample, ]
test   <- data[!sample, ]
rm(sample)

# Initiating model
model<-lm(Memory_Bandwidth~.,data=train)
summary(model)

model$coefficients

prediction<-predict(model, newdata=test)
comparison<-data.frame('Predicted values'=prediction,'Actual values'=test$Memory_Bandwidth)

SSE <- sum((test$Memory_Bandwidth - prediction)^2)
SST <- sum((test$Memory_Bandwidth - mean(test$Memory_Bandwidth))^2)
accuracy<-(SST - SSE) / (SST)

# Mo rong______________________________________________________________________________________________________________
# Polynomial (Quadratic)
modelP2<-lm(Memory_Bandwidth~poly(Core_Speed,2)+poly(Max_Power,2)+poly(Memory,2)+poly(Memory_Bus,2)+poly(Memory_Speed,2)+poly(Pixel_Rate,2)+poly(Process,2)+poly(Release_Date,2),data=train)

prediction<-predict(modelP2, newdata=test)
comparison<-data.frame('Predicted values'=prediction,'Actual values'=test$Memory_Bandwidth)

SSE <- sum((test$Memory_Bandwidth - prediction)^2)
SST <- sum((test$Memory_Bandwidth - mean(test$Memory_Bandwidth))^2)
accuracyP2<-(SST - SSE) / (SST)

# Polynomial (Quartic)
modelP4<-lm(Memory_Bandwidth~poly(Core_Speed,4)+poly(Max_Power,4)+poly(Memory,4)+poly(Memory_Bus,4)+poly(Memory_Speed,4)+poly(Pixel_Rate,4)+poly(Process,4)+poly(Release_Date,4),data=train)

prediction<-predict(modelP4, newdata=test)
comparison<-data.frame('Predicted values'=prediction,'Actual values'=test$Memory_Bandwidth)

SSE <- sum((test$Memory_Bandwidth - prediction)^2)
SST <- sum((test$Memory_Bandwidth - mean(test$Memory_Bandwidth))^2)
accuracyP4<-(SST - SSE) / (SST)

rm(SSE,SST,comparison)

# Multiple multivariate
model1<-lm(cbind(Memory_Speed,Memory_Bandwidth)~Core_Speed+Max_Power+Memory+Memory_Bus+Pixel_Rate+Process+Release_Date,data=data)
summary(model1)

coef(model1)

Anova(model1)

prediction<-predict(model1, newdata=test)



  

