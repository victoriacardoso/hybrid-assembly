import sys
path=sys.argv[1]
f=open(path+'/Wait2Process.txt')
fw=open(path+'/Extend_h.txt','w')
fw1=open(path+'/Extend_t.txt','w')
fw2=open(path+'/Wait2Process_1.txt','w')
my_blast=open(path+'/result_blast.txt')#20120420
fw3=open(path+'/Repeat_Region.txt','w')
my_thr=float(sys.argv[2])
head=[];data=[];tail=[]
reciprocal=dict()
max_h=0
max_t=0
def remove_dupes(seq): # ********** order preserving
    noDupes = []
    [noDupes.append(i) for i in seq if not noDupes.count(i)]
    return noDupes
while True:
    s=f.readline().replace('\n','')
    if not s:break
    if '>' in s :
        head=[];tail=[];max_info_h='';max_h=0;max_info_t='';max_t=0;data=[];u_head=[];u_tail=[]
        header=s
        data.append(s)
        continue
        #fw.write(s+'\n');continue
    if '#' in s :
        data=remove_dupes(data) # remove Dupes
        head=remove_dupes(head)
        tail=remove_dupes(tail)
        if len(data)>1:
            for row in data:
                if '\t' in row:
                    qs=int(row.split()[2])
                    qe=int(row.split()[3])
                    #20120410
                    ref_l=int(row.split()[0].split(':')[1])
                    align_l=float(row.split()[6])
                    if qs != 1 and qs < int(align_l*0.01) : fw2.write(row+'\n');continue
                    if qe != ref_l and qe > (ref_l-int(align_l*0.01)): fw2.write(row+'\n');continue
                    continue
                fw2.write(row+'\n')
            fw2.write(s+'\n')
        if max_info_h: # She finds a info in which there is a biggest align.
            my_align=float(max_info_h.split()[6])
            IsUsed=True
            for row in head+u_head:
                if row==max_info_h:continue
                align=float(row.split()[6])
                if align/my_align >0.9:IsUsed=False
            if IsUsed:
                temp_q=max_info_h.split()[0]
                if reciprocal.get(temp_q):
                    data=reciprocal.get(temp_q)
                    new_d=int(max_info_h.split()[6])
                    old_d=int(data.split()[6])
                    if new_d>old_d:
                        reciprocal[temp_q]=max_info_h
                else:
                    reciprocal[temp_q]=max_info_h
            #else:
                #fw3.write(header+'\n');
                #for row in head+u_head:
                #    fw3.write(row+'\n');
                #fw3.write(s+'\n');               
        if max_info_t: # She finds a info in which there is a biggest align.
            my_align=float(max_info_t.split()[6])
            IsUsed=True
            for row in tail+u_tail:
                if row==max_info_t:continue
                align=float(row.split()[6])
                if align/my_align >0.9:IsUsed=False
            if IsUsed:
                temp_q=max_info_t.split()[0]
                if reciprocal.get(temp_q):
                    data=reciprocal.get(temp_q)
                    new_d=int(max_info_t.split()[6])
                    old_d=int(data.split()[6])
                    if new_d>old_d:
                        reciprocal[temp_q]=max_info_t
                else:
                    reciprocal[temp_q]=max_info_t
            #else:
                #fw3.write(header+'\n');
                #for row in tail+u_tail:
                #    fw3.write(row+'\n');
                #fw3.write(s+'\n');
        continue
    qs=int(s.split()[2])
    qe=int(s.split()[3])
    ss=int(s.split()[4])
    se=int(s.split()[5])
    ref_l=int(s.split()[0].split(':')[1])
    align_l=int(s.split()[6])
    contig_l=int(s.split()[1].split(':')[1])
    if qs==1 and align_l<contig_l:
        if ss==1 or ss==contig_l:u_head.append(s);continue
        if se!=1 and se!=contig_l:u_head.append(s);continue#20120419 This condition is not in Filter, is in Connect, but this program need process first
        if int(align_l)>max_h:
            max_h=align_l;max_info_h=s
        head.append(s);continue
    if qe==ref_l and align_l<contig_l:
        if se==1 or se==contig_l:u_tail.append(s);continue
        if ss!=1 and ss!=contig_l:u_tail.append(s);continue#20120419 This condition is not in Filter, is in Connect, but this program need process first
        if int(align_l)>max_t:
            max_t=align_l;max_info_t=s
        tail.append(s);continue
    if qs==1 or qe==ref_l:data.append(s);continue # for count to remove
    if len(head)==0 and len(tail)==0:
        data.append(s) # no head and tail extend , # no for count to remove, # For Trim
    #fw.write(s+'\n')
for key_1 in reciprocal.keys():
    extend_data_1=reciprocal.get(key_1)
    key_2=extend_data_1.split()[1]
    extend_data_2=reciprocal.get(key_2)
    if not extend_data_2:continue
    key_from_2=extend_data_2.split()[1]
    if key_1==key_from_2:
        qs=int(extend_data_1.split()[2])
        qe=int(extend_data_1.split()[3])
        if qs==1 or qe ==1:
            fw.write('>'+key_1+'\n');fw.write(extend_data_1+'\n');fw.write('#\n')
        else:
            fw1.write('>'+key_1+'\n');fw1.write(extend_data_1+'\n');fw1.write('#\n')
        reciprocal.pop(key_1)
f.close();fw.close();fw1.close();fw2.close();
#Get Repetitive region 20120420
my_dict=dict()
my_head=''
for i in my_blast:
    if '# Query:' in i:my_head=i.replace('\n','').split()[2]
    if '#' in i and my_dict:
        my_list=[]
        for key in my_dict.keys():
            if len(my_dict[key])<2:continue
            for data in my_dict[key]:
                my_list.append(data)
        if len(my_list):
            fw3.write('>'+my_head+'\n')
            for data in my_list:
                fw3.write(data)
            fw3.write('#\n')
        my_dict=dict()
        my_head=''
        continue
    if '#' in i:continue
    tmp=i.split()
    if tmp[0]==tmp[1]:continue
    my_qsqe=tmp[2]+','+tmp[3]
    if my_dict.get(my_qsqe):
        my_dict[my_qsqe].append(i)
    else:
        my_dict[my_qsqe]=[]
        my_dict[my_qsqe].append(i)
#
fw3.close();my_blast.close()
