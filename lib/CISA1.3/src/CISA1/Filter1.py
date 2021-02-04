import sys
current_p=sys.argv[1]
f=open(current_p+'/CISA1/Wait2Process.txt')
fw=open(current_p+'/CISA1/Extend_h.txt','w')
fw1=open(current_p+'/CISA1/Extend_t.txt','w')
fw2=open(current_p+'/CISA1/Wait2Process_1.txt','w')
head=[];data=[];tail=[]
max_h=0
max_t=0
while True:
    s=f.readline().replace('\n','')
    if not s:break
    if '>' in s :
        head=set();tail=set();max_h=0;max_t=0;data=[]
        header=s
        data.append(s)
        continue
        #fw.write(s+'\n');continue
    if '#' in s :
        if len(data)>1:
            for row in data:
                fw2.write(row+'\n')
            fw2.write(s+'\n')            
        if len(head)>1:# Use the data in which have longest contig
            fw.write(header+'\n');
            for row in head:
                contig_l=row.split('\t')[1].split(':')[1]
                if int(contig_l)==max_h:
                    fw.write(row+'\n');break
            fw.write(s+'\n');
        elif len(head)==1:
            fw.write(header+'\n');
            fw.write(head.pop()+'\n')
            fw.write(s+'\n');
        if len(tail)>1:
            fw1.write(header+'\n');
            for row in tail:
                contig_l=row.split('\t')[1].split(':')[1]
                if int(contig_l)==max_t:
                    fw1.write(row+'\n');break
            fw1.write(s+'\n');
        elif len(tail)==1:
            fw1.write(header+'\n');
            fw1.write(tail.pop()+'\n')
            fw1.write(s+'\n');
        continue
    s1=int(s.split(' ')[0])
    e1=int(s.split('|')[0].split(' ')[1])
    s2=int(s.split('|')[1].split(' ')[1])
    e2=int(s.split('|')[1].split(' ')[2])
    ref_l=int(s.split('\t')[0].split(':')[1])
    align_l=int(s.split('|')[2].split(' ')[2])
    contig_l=int(s.split('\t')[1].split(':')[1])
    if s1==1 and align_l<contig_l:
        if s2==1 or s2==contig_l:continue
        if int(contig_l)>max_h:max_h=int(contig_l)
        head.add(s);continue
    if e1==ref_l and align_l<contig_l:
        if e2==1 or e2==contig_l:continue
        if int(contig_l)>max_t:max_t=int(contig_l)
        tail.add(s);continue
    if s1==1 or e1==ref_l:continue
    data.append(s)
    #fw.write(s+'\n')
f.close();fw.close();fw1.close();fw2.close()
