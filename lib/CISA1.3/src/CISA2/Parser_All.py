import re
import sys
r2_gap=float(sys.argv[1])
current_p=sys.argv[2]
f=open(current_p+'/info')
try:
    end=int(f.readline().replace('\n','').split(':')[1])
except ValueError:
    print 'Needs a correct info!'
f.close()
print 'Range from 1 to '+str(end-1)
fw=open(current_p+'/CISA2/clip_info','w')
fw_gap=open(current_p+'/CISA2/Gaps','w')
d=dict()
my_all_data=[]
count=1
for i in range(1,end):
    print "Start to process %d" % i
#20120412 To get that total align length doesn't reach 95%
    query_total_len=dict()#20120412
    atHeadTail=dict()
    ht_range=0
    unreachable=[]
    f=open(current_p+'/CISA2/result'+str(i)+'.coords')
    for j in range(5):
        s=f.readline()
    while True:
        s=f.readline().replace('\n','')
        if not s:break
        if not ht_range: Ref_l=int(s.split()[-2].split(':')[1])
        if Ref_l/1000 <1:ht_range=1 # head and tail ranges are 0.001 * ref_length
        else : ht_range=int(Ref_l/1000)
        query_len=int(s.split()[7])
        query_id=s.split()[-1]
        if query_total_len.get(query_id):
            query_total_len[query_id]+=query_len
        else:
            query_total_len[query_id]=query_len
        s1=int(s.split()[0])
        e1=int(s.split()[1])
        if s1==1 or (s1 >1 and s1<=ht_range):atHeadTail[query_id]=True
        if e1==Ref_l or (e1 <Ref_l and e1>=(Ref_l-ht_range)):atHeadTail[query_id]=True
    f.close()
    for my_k in query_total_len.keys():
        if atHeadTail.get(my_k):continue
        my_len=0;my_align=0
        my_len=float(my_k.split(':')[1])
        my_align=float(query_total_len[my_k])
        if my_len and my_align:
            if (my_align/my_len)<0.95:
                unreachable.append(my_k)
#20120412    
    f=open(current_p+'/CISA2/result'+str(i)+'.coords')
    for j in range(5):
        s=f.readline()
    while True:
        s=f.readline()
        if not s:break
        #20120411
        idy=float(s.split()[-4])
        if idy < 0.95 : continue
        query_id=s.split()[-1].replace('\n','')
        if query_id in unreachable:continue
        #20120411
        s_n=re.sub(' +',' ',s)
        if s_n[0]==' ':s_n=s_n.replace(' ','',1)
        ref_id=s_n.split('|')[4].split('\t')[0].replace(' ','')
        ref_l=ref_id.split(':')[1]
        query_id=s_n.split('|')[4].split('\t')[1].replace('\n','')
        if ref_id==query_id:continue
        temp=s_n.split('|')[0].split(' ')
        range_1=temp[0]+','+temp[1]
        if temp[1]=='' or temp[0]==''  :print ref_id+' '+s_n
        if d.get(ref_id):
            temp=d.get(ref_id)
            d[ref_id]=temp+';'+range_1
        else:
            d[ref_id]=ref_l+'|'+range_1
    f.close()
#20120420
    my_dict_h=dict()
    my_dict_t=dict()
    f=open(current_p+'/CISA2/result'+str(i)+'.coords')
    for j in range(5):
        s=f.readline()
    Ref_len=0
    while True:
        s=f.readline().replace('\n','')
        if not s:break
        tmp=s.split()
        if not Ref_len:
            Ref_len=float(tmp[-2].split(':')[1])
        s1=int(tmp[0])
        s2=int(tmp[1])
        if tmp[-1]==tmp[-2]:continue
        align_l=float(tmp[7])
        query_partial_name=re.sub('_\d+_len','',tmp[-1].split(':')[0])
        if (s1==1 or s1 <= Ref_len*0.01) and (align_l/Ref_len)>0.1:
        #if s1==1:
            if my_dict_h.get(query_partial_name):
                my_dict_h[query_partial_name].append(s)
            else:
                my_dict_h[query_partial_name]=[]
                my_dict_h[query_partial_name].append(s)
            continue
        if (s2==Ref_len or s2 >= Ref_len-(Ref_len*0.01) ) and (align_l/Ref_len)>0.1:
        #if s2==Ref_len:
            if my_dict_t.get(query_partial_name):
                my_dict_t[query_partial_name].append(s)
            else:
                my_dict_t[query_partial_name]=[]
                my_dict_t[query_partial_name].append(s)
            continue           
    f.close()
    my_tmp_list=[]#20120423, to record incorrect seq to SplitInfo
    for key in my_dict_h.keys():
        if my_dict_h.get(key) and my_dict_t.get(key):
            my_tmp1_list=[];max_h=0;max_t=0;max_h_data='';max_t_data='' # if a set has multiple head or tail , pick up a data which is with max align_len
            my_tmp_align=0
            for x in my_dict_h[key]:
                my_tmp_data=x.split()
                my_tmp_align=int(my_tmp_data[7])
                if my_tmp_align>max_h:
                    max_h=my_tmp_align;max_h_data=x
            for x in my_dict_t[key]:
                my_tmp_data=x.split()
                my_tmp_align=int(my_tmp_data[7])
                if my_tmp_align>max_t:
                    max_t=my_tmp_align;max_t_data=x
            my_tmp1_list.append(max_h_data+'\n');my_tmp1_list.append(max_t_data+'\n')
            used_data=True
            for j in my_tmp1_list:#check if subject is in middle
                tmp_j=j.split()
                ss=int(tmp_j[3])
                se=int(tmp_j[4])
                s_len=int(tmp_j[-1].split(':')[1].replace('\n',''))
                if ss==1 or se==s_len or ss==s_len or se==1:used_data=False
            if used_data:my_tmp_list+=my_tmp1_list
    if len(my_tmp_list)>=2:
        my_all_data+=my_tmp_list
fw_split=open(current_p+'/CISA2/Remove_Info','w')
for j in range(0,len(my_all_data),2):
    j1=my_all_data[j]
    j2=my_all_data[j+1]
    tmp_1=j1.split();tmp_2=j2.split()
    j1_se=int(tmp_1[1]);j2_ss=int(tmp_2[0])
    ref_len=float(tmp_1[-2].split(':')[1])
    if (j2_ss-j1_se) <=0 or ((j2_ss-j1_se)>0 and (j2_ss-j1_se)<=(ref_len*0.1)):
        fw_split.write(j1);fw_split.write(j2)
fw_split.close()    

#get thr
thr_score=[]
for key in d.keys():
    contig=""
    data=d.get(key)
    length=int(data.split('|')[0])
    contig=list(length*'-') # At first, whole units in the list are gat '-'
    temp=data.split('|')[1].split(';')
    for row in temp:
        start=int(row.split(',')[0])
        end=int(row.split(',')[1])
        contig[start-1:end]='*'*(end-start+1) # If any contig is mapped there, let the unit change to '*'
    temp_s=''.join(contig)#Only string can use 'replace API'
    if '-' in temp_s:
        [thr_score.append(len(i)) for i in temp_s.split('*') if len(i)>0]
    else:
        continue
which_posi=int(len(thr_score)*(1-r2_gap))
thr_score.sort(reverse=True)
if not thr_score:# no gaps
    fw.close();fw_gap.close()
    exit(0)
r2_gap=thr_score[which_posi]
#Start to process gap
fw_gap.write('Gap_Thr: '+str(r2_gap)+'\n')
for key in d.keys():
    contig=""
    data=d.get(key)
    length=int(data.split('|')[0])
    contig=list(length*'-') # At first, whole units in the list are gat '-'
    temp=data.split('|')[1].split(';')
    for row in temp:
        start=int(row.split(',')[0])
        end=int(row.split(',')[1])
        contig[start-1:end]='*'*(end-start+1) # If any contig is mapped there, let the unit change to '*'
    temp_s=''.join(contig)#Only string can use 'replace API'
    if '-' in temp_s:
        fw_gap.write('>'+key+'\n')
        fw_gap.write('\t'.join([str(len(i)) for i in temp_s.split('*') if len(i)>0]))
        fw_gap.write('\n')
    else:
        continue
    for i in range(r2_gap):#gap < 10, skip it, so if gap<10000, change it to '*'
        co=i+1         # this mathod can't process gap in head or tail
        replace_s='*'+'-'*co+'*'  # Notice: because if use '*---',will find '*---------------------*'
        replace_n='*'*(co+2)
        temp_s=temp_s.replace(replace_s,replace_n)
    contig=list(temp_s)
    if '-' in contig:
        c=''.join(contig)
        n_c=re.sub('-+','-',c)
        n_c=re.sub('\*+','*',n_c)
        print key;fw.write('>'+key+'\n')
        print n_c;fw.write(n_c+'\n')
        count=1
        if contig[0]=='-':print '1 ',;fw.write('1 ')
        for i in contig:  # i iterate unit in contig
            try:
                ne=contig[count] # ne iterate unit in front of i in contig
                if i!=ne: # I want to print posi of '-' 
                    if ne=='*':print count,;fw.write(str(count)+' ')  # It means '-*', now count is in posi '-'
                    if ne=='-':print count+1,;fw.write(str(count+1)+' ') # It means '*-' now count is in posi '*', next posi is '-'
                count+=1
            except IndexError:
                if contig[length-1]=='-':
                    fw.write(str(length)+'\n');print length;break
                print;fw.write('\n')
                break
fw.close();fw_gap.close()
