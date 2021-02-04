#! /usr/bin/python
import re
import sys
import os
class FileIntegrator:
    def __init__(self,fs):
        self.master=open(fs,'w')
    def append(self,fi):
        con=[]
        f=open(fi)
        con=f.readlines()
        self.master.writelines(con)
        f.close()
    def close(self):
        self.master.close()
def N50(data_path,f_n50):
    f=open(data_path+'.p.fa')
    d=dict()
    genome=0
    while True:
        h=f.readline()
        if not h:break
        seq=f.readline().replace('\n','')
        d[h]=int(h.split(':')[1])
    f.close()
    d_s=sorted(d.values(),key=lambda x:x,reverse=True)
    for value in d_s:
        genome+=value
    print 'whole:%d'%genome
    f_n50.write('Whole Genome : '+str(genome)+'\n')
    n5=genome/2
    genome=0
    for row in d_s:
        genome+=row
        if genome > n5:
            print 'N50: %d' % row
            f_n50.write('N50 : '+str(row)+'\n')
            break
def convert_title(data_path,title,min_len,f_n50):
    f=open(data_path+'.One_l')
    fw=open(data_path+'.p.fa','w')
    min_len=int(min_len)
    max_len=0
    count=1
    while True:
        h=f.readline().replace('\n','')
        if not h:break
        s=f.readline().replace('\n','').upper()
        if len(s)<min_len:continue
        fw.write('>'+title+'_'+str(count)+'_len:'+str(len(s))+'\n')
        fw.write(s+'\n')
        if len(s)>max_len:max_len=len(s)
        count+=1
    print 'Number of contigs: %d'%(count-1)
    print 'Length of the longest contig: %d'%max_len
    f_n50.write('Number of contigs: '+str(count-1)+'\n')
    f_n50.write('Length of the longest contig: '+str(max_len)+'\n')
    f.close();fw.close()
    
def multi2one(data_path):
    f=open(data_path)
    fw=open(data_path+'.One_l','w')
    temp=''
    for s in f:
        s=s.replace('\n','')
        if '>' in s:
            if temp:fw.write(temp+'\n');temp=''
            header=s
            fw.write(header+'\n')
        else:
            temp=temp+s
    fw.write(temp+'\n')
    f.close();fw.close()
    
def Process_Gap(data_path,title,gaps,min_len,f_n50):
    f=open(data_path+'.One_l')
    fw=open(data_path+'.p.fa','w')
    min_len=int(min_len)
    max_len=0
    count=1
    while True:
        h=f.readline().replace('\n','')
        if not h:break
        s=f.readline().replace('\n','').upper()
        if 'N' in s:
            #t_s=re.sub('N+','N',s).split('N')
            t_s=re.sub('N{'+gaps+',}','=CUT=',s).split('=CUT=') # Up 10 continue N to be cut
            for data in t_s:
                if len(data)<min_len:continue
                fw.write('>'+title+'_'+str(count)+'_len:'+str(len(data))+'\n')
                fw.write(data+'\n')
                if len(data)>max_len:max_len=len(data)
                count+=1
        else:
            if len(s)<min_len:continue
            fw.write('>'+title+'_'+str(count)+'_len:'+str(len(s))+'\n')
            fw.write(s+'\n')
            if len(s)>max_len:max_len=len(s)
            count+=1
    print 'Number of contigs: %d'%(count-1)
    print 'Length of the longest contig: %d'%max_len
    f_n50.write('Number of contigs: '+str(count-1)+'\n')
    f_n50.write('Length of the longest contig: '+str(max_len)+'\n')
    fw.close();f.close()

#f=open('myfile')
if len(sys.argv)!=2:print 'Needs a data file!'
f=open(sys.argv[1])
data=[]
gaps='0'
min_len='100'
while True:
    s=f.readline().replace('\n','')
    if not s:break
    if 'count=' in s:
        temp=int(s.split('=')[1])
        for i in range(temp):
            s=f.readline().replace('\n','')
            data.append(s)
    if 'Master_file=' in s:
        master_file=s.split('=')[1]
    if 'Gap=' in s:
        if s.split('=')[1]:gaps=s.split('=')[1]
    if 'min_length=' in s:
        if s.split('=')[1]:min_len=s.split('=')[1]
f.close()

mf=FileIntegrator(master_file)
f_n50=open('Merge_info','w')
for i in data:
    data_path=i.split(',')[0].split('=')[1]
    title=i.split(',')[1].split('=')[1]
    multi2one(data_path)
    print data_path+'.p.fa'
    f_n50.write(data_path+'.p.fa'+'\n')
    if gaps=='0':
        convert_title(data_path,title,min_len,f_n50)
    else:
        Process_Gap(data_path,title,gaps,min_len,f_n50)
    mf.append(data_path+'.p.fa')
    N50(data_path,f_n50)
    os.remove(data_path+'.One_l');#os.remove(data_path+'.p.fa')
mf.close();f_n50.close()

