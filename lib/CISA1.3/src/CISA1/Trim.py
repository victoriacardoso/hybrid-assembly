import sys
current_p=sys.argv[1]
"""20120413
f=open(current_p+'/CISA1/Contigs_E.fa')
f1=open(current_p+'/CISA1/Wait2Process_1.txt')
f_c=open(current_p+'/R1_Contigs_E_T.fa','w')

d=dict()
while True:
    h=f.readline().replace('\n','')
    if not h:break
    seq=f.readline().replace('\n','')
    #h=h.split(' ')[0]# For process => >Newbler_contig10187_length:6231   numreads=28124
    #This stage have been processed in Extend stage.
    h=h.split(':')[0]#some contigs have beed extended, its len have been changed.
    d[h]=seq.upper()
switch_b=False
while True:
    s=f1.readline()
    if not s:break
    if '>' in s:
        R_key=s.split(',')[0]
        start=0;end=0 # I suppose no contig bigger than this number, If some contig bigger than it,  I need to adjust it bigger.
        R_key=R_key.split(' ')[0]# For process => >Newbler_contig10187_length:6231   numreads=28124
        R_key=R_key.split(':')[0]#some contigs have beed extended.
        continue
    if '#' in s:
        print R_key
        if end != 0: # Trim End_Posi first, because End_Posi will be effect by Start_Posi.
            #print end    #
            new_seq=d.get(R_key)[:-end]
            d[R_key]=new_seq        
        if start != 0:
            #print start
            new_seq=d.get(R_key)[start-1:]
            d[R_key]=new_seq
        continue
    s1=int(s.split(' ')[0])
    e1=int(s.split('|')[0].split(' ')[1])
    s2=int(s.split('|')[1].split(' ')[1])
    e2=int(s.split('|')[1].split(' ')[2])
    ref_l=int(s.split('\t')[0].split(':')[1])
    ref_l_real=len(d[R_key])
    align_l=int(s.split('|')[2].split(' ')[2])
    contig_l=int(s.split('\t')[1].split(':')[1])
    if ref_l<500:continue  # head and tail trim windows size are 200 individualy, if ref_l <400 ,
    if s1 < 200 and s1>start:  # it cause the window overlap and then error appears, I use < 500 , because I think bigger is safer.
        start=s1
    if (ref_l-e1)<200 and (ref_l-e1)>end:
        end=(ref_l-e1)
for key in d.keys():
    seq=d.get(key)
    new_key=key+(':')+str(len(seq))
    f_c.write(new_key+'\n')
    f_c.write(seq+'\n')
f.close();f1.close();f_c.close()
"""#20120413
#---Parser
f=open(current_p+'/R1_Contigs_E_T.fa')
f1=open(current_p+'/CISA1/Wait2Process.txt')
fw=open(current_p+'/R1_Contigs.fa','w')
d=dict()
while True:
    h=f.readline().replace('\n','')
    if not h:break
    seq=f.readline().replace('\n','')
    h=h.split(':')[0]
    d[h]=seq
while True:
    s=f1.readline()
    if not s:break
    if '>' in s:
        key=s.split(':')[0]
        seq=d.get(key)
        fw.write(key+':'+str(len(seq))+'\n')
        fw.write(seq+'\n')
f.close();f1.close();fw.close()
