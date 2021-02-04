#! /usr/bin/python
import sys
import os
if len(sys.argv)!=2:print 'Please give a file!'
myfile=sys.argv[1]
f=open(myfile)
nucmer='nucmer'
makeblastdb='makeblastdb'
blastn='blastn'
r2_gap='0.95'
outfile='CISA_Out.fa'
for i in f:
    if 'genome=' in i:
        g_l=i.split('=')[1].replace('\n','')
    if 'infile=' in i:
        infile=i.split('=')[1].replace('\n','')
    if 'outfile=' in i:
        outfile=i.split('=')[1].replace('\n','')
    if 'nucmer=' in i:
        nucmer=i.split('=')[1].replace('\n','')
    if 'makeblastdb=' in i:
        makeblastdb=i.split('=')[1].replace('\n','')
    if 'blastn=' in i:
        blastn=i.split('=')[1].replace('\n','')
    if 'R2_Gap=' in i:
        r2_gap=i.split('=')[1].replace('\n','')
    if 'CISA=' in i:
        CISA_S=i.split('=')[1].replace('\n','')+'/src'
current_p=os.getcwd()
sys.path.append(CISA_S+'/CISA1')
from Controllor_R1 import R1
if not os.path.exists('CISA1'):os.makedirs('CISA1')
my_work=R1()
my_work.Start(g_l,infile,nucmer,CISA_S,current_p)
sys.path.append(CISA_S+'/CISA2')
if not os.path.exists('CISA2'):os.makedirs('CISA2')
from Controllor_R2 import R2
my_work=R2()
my_work.Start(nucmer,r2_gap,CISA_S,current_p)
sys.path.append(CISA_S+'/CISA3')
if not os.path.exists('CISA3'):os.makedirs('CISA3')
from Controllor_R3 import R3
my_work=R3()
my_work.Start(CISA_S,current_p,makeblastdb,blastn)
f=open('info1')
for data in f:
    if 'Round3_result=' in data:R4_start=data.split('=')[1].replace('\n','')
    if 'Repeat_region=' in data:thr=data.split('=')[1].replace('\n','')
f.close()
final=R4_start
if int(thr)>0:
    sys.path.append(CISA_S+'/CISA4')
    if not os.path.exists('CISA4'):os.makedirs('CISA4')
    from Controllor_R4 import R4
    my_work=R4()
    my_work.Start(thr,R4_start,CISA_S,current_p,makeblastdb,blastn)
    f=open('info2')
    for data in f:
        if 'Round4_result=' in data:R4_end=data.split('=')[1].replace('\n','')
    f.close()
    if R4_end:final=R4_end

os.rename('Contigs_'+final+'.fa',outfile)
final=int(final)
for i in range(1,final):
    if os.path.exists('Contigs_'+str(i)+'.fa'):os.remove('Contigs_'+str(i)+'.fa')
