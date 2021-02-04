import sys
def comp(temp):
    s_r=[]
    s_l=list(temp)
    s_l.reverse()
    for i in s_l:
        if i=='A':s_r.append('T')
        if i=='T':s_r.append('A')
        if i=='C':s_r.append('G')
        if i=='G':s_r.append('C')
    return ''.join(s_r)
f=open(sys.argv[1])
current_p=sys.argv[2]
f1=open(current_p+'/CISA1/Extend_h.txt')
f2=open(current_p+'/CISA1/Extend_t.txt')
#20120412
#f_c=open(current_p+'/CISA1/Contigs_E.fa','w')
f_c=open(current_p+'/R1_Contigs_E_T.fa','w')
ex_info=open(current_p+'/CISA1/Extend_info','w')
test=open(current_p+'/CISA1/ForCheck_E_h.fa','w')
test1=open(current_p+'/CISA1/ForCheck_E_t.fa','w')
d=dict()
while True:
    h=f.readline().replace('\n','')
    if not h:break
    seq=f.readline().replace('\n','')
    h=h.split(' ')[0]# For process => >Newbler_contig10187_length:6231   numreads=28124
    d[h]=seq.upper()
while True:
    s=f1.readline()
    if not s:break
    R_key=s.split(',')[0]
    R_key=R_key.split(' ')[0]# For process => >Newbler_contig10187_length:6231   numreads=28124
    data=f1.readline().replace('\n','')
    Q_key='>'+data.split('\t')[1]
    end=f1.readline().replace('\n','')
    s1=int(data.split(' ')[0])
    e1=int(data.split('|')[0].split(' ')[1])
    s2=int(data.split('|')[1].split(' ')[1])
    e2=int(data.split('|')[1].split(' ')[2])
    ref_l=int(data.split('\t')[0].split(':')[1])
    align_l=int(data.split('|')[2].split(' ')[2])
    contig_l=int(data.split('\t')[1].split(':')[1])
    if s1==1 and s2<e2 and align_l<contig_l:
        if s2 <= 10:continue
        extend_range=d.get(Q_key)[:s2-1]
        temp=d.get(R_key)
        d[R_key]=extend_range+temp
        ex_info.write('Head:\t'+R_key+'\t'+Q_key+'\n')
        test.write(R_key+'\n');test.write(extend_range+temp+'\n')
    if s1==1 and s2>e2 and align_l<contig_l:
        if (contig_l-align_l)<=10:continue
        extend_range=d.get(Q_key)[-(contig_l-align_l):]
        extend_range=comp(extend_range)
        temp=d.get(R_key)
        d[R_key]=extend_range+temp
        ex_info.write('Head:\t'+R_key+'\t'+Q_key+'\n')
        test.write(R_key+'\n');test.write(extend_range+temp+'\n')
while True:
    s=f2.readline()
    if not s:break
    R_key=s.split(',')[0]
    R_key=R_key.split(' ')[0]# For process => >Newbler_contig10187_length:6231   numreads=28124
    data=f2.readline().replace('\n','')
    Q_key='>'+data.split('\t')[1]
    end=f2.readline().replace('\n','')
    s1=int(data.split(' ')[0])
    e1=int(data.split('|')[0].split(' ')[1])
    s2=int(data.split('|')[1].split(' ')[1])
    e2=int(data.split('|')[1].split(' ')[2])
    ref_l=int(data.split('\t')[0].split(':')[1])
    align_l=int(data.split('|')[2].split(' ')[2])
    contig_l=int(data.split('\t')[1].split(':')[1])
    if e1==ref_l and s2<e2 and align_l<contig_l:
        if (contig_l-align_l)<=10:continue
        extend_range=d.get(Q_key)[-(contig_l-align_l):]
        temp=d.get(R_key)
        d[R_key]=temp+extend_range
        ex_info.write('Tail:\t'+R_key+'\t'+Q_key+'\n')
        test1.write(R_key+'\n');test1.write(temp+extend_range+'\n')
    if e1==ref_l and s2>e2 and align_l<contig_l:
        if e2<=10:continue
        extend_range=d.get(Q_key)[:e2-1]
        extend_range=comp(extend_range)
        temp=d.get(R_key)
        d[R_key]=temp+extend_range
        ex_info.write('Tail:\t'+R_key+'\t'+Q_key+'\n')
        test1.write(R_key+'\n');test1.write(temp+extend_range+'\n')
for key in d.keys():
    seq=d.get(key)
    new_key=key.split(':')[0]+(':')+str(len(seq))
    f_c.write(new_key+'\n')
    f_c.write(seq+'\n')
f.close();f1.close();f2.close();test.close();test1.close();ex_info.close()
