import sys
path=sys.argv[1]
file_number=sys.argv[2]
current_p=sys.argv[3]
my_thr=float(sys.argv[4])
f=open(path+'/temp.fa')
f1=open(path+'/Wait2Process_1.txt')
f_c=open(current_p+'/Contigs_'+str(int(file_number)+1)+'.fa','w')
#f_c=open('Contigs_E_T.fa','w')

d=dict()
while True:
    h=f.readline().replace('\n','')
    if not h:break
    seq=f.readline().replace('\n','')
    #h=h.split(' ')[0]# For process => >Newbler_contig10187_length:6231   numreads=28124
    #This stage have been processed in Extend stage.
    d[h]=seq.upper()
switch_b=False
while True:
    s=f1.readline()
    if not s:break
    if '>' in s:
        R_key=s.replace('\n','')
        start=0;end=500000000 # I suppose no contig bigger than this number, If some contig bigger than it,  I need to adjust it bigger.
        continue
    if '#' in s:
        print R_key
        if end != 500000000: # Trim End_Posi first, because End_Posi will be effect by Start_Posi.
            #print end    #
            if not d.get(R_key): continue # it has been remove in Connect_Remove.py
            new_seq=d.get(R_key)[:end]
            d[R_key]=new_seq        
        if start != 0:
            #print start
            if not d.get(R_key): continue # it has been remove in Connect_Remove.py
            new_seq=d.get(R_key)[start-1:]
            d[R_key]=new_seq
        continue
    qs=int(s.split()[2])
    qe=int(s.split()[3])
    ss=int(s.split()[4])
    se=int(s.split()[5])
    ref_l=int(s.split()[0].split(':')[1])
    align_l=int(s.split()[6])
    contig_l=int(s.split()[1].split(':')[1])
    if qs>start and qs < (0.01*align_l):  
        start=qs
    if qe<end and qe > (ref_l-(0.01*align_l)):
        end=qe    
for key in d.keys():
    seq=d.get(key)
    new_key=key.split(':')[0]+(':')+str(len(seq))
    f_c.write(new_key+'\n')
    f_c.write(seq+'\n')
f.close();f1.close();f_c.close()
