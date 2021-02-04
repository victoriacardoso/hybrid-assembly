import sys
path=sys.argv[1]
file_number=sys.argv[2]
current_p=sys.argv[3]
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
f=open(current_p+'/Contigs_'+file_number+'.fa')
f1=open(path+'/Extend_h.txt')
f2=open(path+'/Extend_t.txt')
f3=open(path+'/result_blast.txt')
f_c=open(path+'/temp.fa','w')
ex_info=open(path+'/Extend_info','w')
f_trim=open(path+'/Wait2Process_1.txt')
test=open(path+'/ForCheck_E_h.fa','w')
test1=open(path+'/ForCheck_E_t.fa','w')
d=dict()
while True:
    h=f.readline().replace('\n','')
    if not h:break
    seq=f.readline().replace('\n','')
    h=h.split(' ')[0]# For process => >Newbler_contig10187_length:6231   numreads=28124
    d[h]=seq.upper()
wait_2_be_trim=[]
while True: # this id has other usage.
    data=f_trim.readline()
    if not data:break
    if '>' in data or '#' in data:continue
    wait_2_be_trim.append('>'+data.split()[0])
    wait_2_be_trim.append('>'+data.split()[1])
while True:
    data=f1.readline()
    if not data:break
    if '>' in data or '#' in data:continue
    wait_2_be_trim.append('>'+data.split()[0])
    wait_2_be_trim.append('>'+data.split()[1])
while True:
    data=f2.readline()
    if not data:break
    if '>' in data or '#' in data:continue
    wait_2_be_trim.append('>'+data.split()[0])
    wait_2_be_trim.append('>'+data.split()[1])
f1.close();f2.close()
f1=open(path+'/Extend_h.txt')
f2=open(path+'/Extend_t.txt')
############################### process Remove####################
score_d={}
while True:
    temp_q=''
    while True:
        s=f3.readline();
        if ('# BLAST processed' in s) or (not s):break
        if 'hits found' in s:break
        if '# Query:' in s:temp_q=s
    if ('# BLAST processed' in s) or (not s):break
    area=''
    q_len=int(temp_q.split()[2].split(':')[1])
    q_id=temp_q.split()[2].replace('\n','')
    area='-'*(q_len-1)
    area=list(area)
    try:
        hits=int(s.split()[1])
    except ValueError:
        print 'She needs a number but you give ' + s.split()[1]
        #if type(hits)!=int:print 'This number is not int!';break    
    temp_d={}
    for i in range(hits): # If there two or more the same sids, sid which has biggst align len stands for removing
        s=f3.readline()
        if '# ' in s :break 
        sid=s.split()[1]
        align_len=int(s.split()[-3])
        temp_s=temp_d.get(sid)
        if temp_s:
            alen=int(temp_s.split()[-3])
            if align_len>alen:temp_d[sid]=s
        else:
            temp_d[sid]=s
    for key in temp_d.keys():
        s=temp_d.get(key)
        qid=s.split()[0];sid=s.split()[1]
        if qid==sid:continue
        align_len=float(s.split()[-3])
        q_len=float(s.split()[0].split(':')[1])
        s_len=float(s.split()[1].split(':')[1])
        qs=int(s.split()[2])
        qe=int(s.split()[3])
        #if (align_len/q_len)<0.3 and (align_len/s_len)<0.3:continue
        area[qs-1:qe]='*'*(qe-qs+1)
    Q_key='>'+q_id
    score_d[Q_key]=(float(area.count('*'))/float(q_len))  
for key in score_d.keys():
    if (score_d.get(key))<1:continue
    if key in wait_2_be_trim:continue
    d.pop(key)
########################## process Remove######
    
while True: #process extend head
    s=f1.readline()
    if not s:break
    R_key=s.replace('\n','')
    data=f1.readline().replace('\n','')
    Q_key='>'+data.split()[1]
    end=f1.readline().replace('\n','')
    qs=int(data.split()[2])
    qe=int(data.split()[3])
    ss=int(data.split()[4])
    se=int(data.split()[5])
    ref_l=int(data.split()[0].split(':')[1])
    align_l=int(data.split()[6])
    contig_l=int(data.split()[1].split(':')[1])
    if qs==1 and ss<se and align_l<contig_l and se==contig_l:#20120904
        if ss <= 10:continue
        if not d.get(Q_key):continue  # This Id has been deleted Or has been used to Extend in this run
        if not d.get(R_key):continue  # This Id has been deleted Or has been used to Extend in this run
        extend_range=d.get(Q_key)[:ss-1]
        temp=d.get(R_key)
        d[R_key]=extend_range+temp
        d.pop(Q_key)  # after extneding, drop Q_key
        ex_info.write('Head:\t'+R_key+'\t'+Q_key+'\n')
        test.write(R_key+'\n');test.write(extend_range+temp+'\n')
    if qs==1 and ss>se and align_l<contig_l and se==1:#20120904 #ref_l = query_contig len, contig_l = subject_contig len
        if (contig_l-align_l)<=10:continue
        if not d.get(Q_key):continue  # This Id has been deleted
        if not d.get(R_key):continue  # This Id has been deleted
        extend_range=d.get(Q_key)[-(contig_l-align_l):]
        extend_range=comp(extend_range)
        temp=d.get(R_key)
        d[R_key]=extend_range+temp
        d.pop(Q_key) # after extneding, drop Q_key
        ex_info.write('Head:\t'+R_key+'\t'+Q_key+'\n')
        test.write(R_key+'\n');test.write(extend_range+temp+'\n')
while True:
    s=f2.readline()
    if not s:break
    R_key=s.replace('\n','')
    data=f2.readline().replace('\n','')
    Q_key='>'+data.split()[1]
    end=f2.readline().replace('\n','')
    qs=int(data.split()[2])
    qe=int(data.split()[3])
    ss=int(data.split()[4])
    se=int(data.split()[5])
    ref_l=int(data.split()[0].split(':')[1])
    align_l=int(data.split()[6])
    contig_l=int(data.split()[1].split(':')[1])
    if qe==ref_l and ss<se and align_l<contig_l and ss==1:#20120904 #ref_l = query_contig len, contig_l = subject_contig len
        if (contig_l-align_l)<=10:continue
        if not d.get(Q_key):continue  # This Id has been deleted
        if not d.get(R_key):continue  # This Id has been deleted
        extend_range=d.get(Q_key)[-(contig_l-align_l):]
        temp=d.get(R_key)
        d[R_key]=temp+extend_range
        d.pop(Q_key) # after extneding, drop Q_key
        ex_info.write('Tail:\t'+R_key+'\t'+Q_key+'\n')
        test1.write(R_key+'\n');test1.write(temp+extend_range+'\n')
    if qe==ref_l and ss>se and align_l<contig_l and ss==contig_l:#20120904 #ref_l = query_contig len, contig_l = subject_contig len
        if se<=10:continue
        if not d.get(Q_key):continue  # This Id has been deleted
        if not d.get(R_key):continue  # This Id has been deleted
        extend_range=d.get(Q_key)[:se-1]
        extend_range=comp(extend_range)
        temp=d.get(R_key)
        d[R_key]=temp+extend_range
        d.pop(Q_key) # after extneding, drop Q_key
        ex_info.write('Tail:\t'+R_key+'\t'+Q_key+'\n')
        test1.write(R_key+'\n');test1.write(temp+extend_range+'\n')
for key in d.keys():
    seq=d.get(key)
    new_key=key.split(':')[0]+(':')+str(len(seq))
    f_c.write(new_key+'\n')
    f_c.write(seq+'\n')
f.close();f1.close();f2.close();test.close();test1.close();f3.close();f_trim.close();ex_info.close()
