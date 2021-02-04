import sys
import os
current_p=sys.argv[1]
f=open(current_p+'/R1_Contigs_E_T.fa')
d=dict()
while True:
    h=f.readline().replace('\n','')
    if not h:break
    seq=f.readline().replace('\n','')
    d[h]=seq
f.close()
f=open(current_p+'/R1_Contigs.fa')
d1=dict()
while True:
    h=f.readline().replace('\n','')
    if not h:break
    seq=f.readline().replace('\n','')
    d1[h]=seq   
f.close()

#20120423, pop incorrect seq and then add new seq into dict "d1"
fw_split=open(current_p+'/CISA2/Remove_Info')
my_all_data=[]
for j in fw_split:my_all_data.append(j.replace('\n',''))
fw_split.close()
for j in range(0,len(my_all_data),2):
    j1=my_all_data[j]
    j2=my_all_data[j+1]
    tmp_1=j1.split();tmp_2=j2.split()
    j1_Ref=tmp_1[-2].split('len:')[0]
    Ref_isin=False
    j1_Sub=tmp_1[-1].split('len:')[0]
    j2_Sub=tmp_2[-1].split('len:')[0]
    j1_inDict=False;j2_inDict=False
    for key in d1.keys():
        if j1_Sub in key:j1_inDict=True;continue
        if j2_Sub in key:j2_inDict=True;continue
        if j1_Ref in key:
            Ref_isin=True
            j1_Ref=key;
            continue
    if not Ref_isin:continue# if the Ref have been removed, continue
    if j1_inDict == False and j2_inDict == False:continue # skip the 2 ids when they are without both
    if j1_inDict and j2_inDict:
        if d1.get(j1_Ref):d1.pop(j1_Ref) 
    if not j1_inDict:
        if d1.get(j1_Ref):d1.pop(j1_Ref)
        for j_key in d.keys():
            if j1_Sub in j_key:d1[j_key]=d[j_key];break
    if not j2_inDict:
        if d1.get(j1_Ref):d1.pop(j1_Ref)
        for j_key in d.keys():
            if j2_Sub in j_key:d1[j_key]=d[j_key];break      
#

f=open(current_p+'/CISA2/clip_info')
fw1=open(current_p+'/CISA2/clip_out','w')
while True:
    h=f.readline().replace('\n','')
    if not h:break
    pattern=f.readline().replace('\n','')
    posi=f.readline().replace('\n','')
    if not d1.get(h):continue # if the title has been removed , continue
    if pattern.count('-')==1:
        if pattern=='-*':
            p=int(posi.split(' ')[1])
            temp=d.get(h)
            n_c=temp[p:] # Catch p+1 ~ bottom, Use [p:]
            n_h=h.split(':')[0]+'_T:'+str(len(n_c))
            d.pop(h);d1.pop(h)
            d[n_h]=n_c;d1[n_h]=n_c
            print n_h
        if pattern=='*-':
            p=int(posi.split(' ')[0])
            temp=d.get(h)
            n_c=temp[:p-1] # Catch head ~ p-1, Use [:p-1]
            n_h=h.split(':')[0]+'_T:'+str(len(n_c))
            d.pop(h);d1.pop(h)
            d[n_h]=n_c;d1[n_h]=n_c
            print n_h
        if pattern=='*-*':
            start=int(posi.split(' ')[0])
            end=int(posi.split(' ')[1])
            temp=d.get(h)
            n_c1=temp[:start-1]
            n_c2=temp[end:]
            d.pop(h);d1.pop(h)
            n_h1=h.split(':')[0]+'_S_1:'+str(len(n_c1))
            n_h2=h.split(':')[0]+'_S_2:'+str(len(n_c2))
            print n_h1
            print n_h2
            d[n_h1]=n_c1;d1[n_h1]=n_c1
            d[n_h2]=n_c2;d1[n_h2]=n_c2
    if pattern.count('-')>1:
        my_thr=0.4
        if pattern.count('-')==2 and pattern.count('*')==2:my_thr=0.05
        posi_l=posi.split()
        posi_l.insert(0,'0')
        c_len=int(h.split(':')[1])
        posi_l.append(str(c_len+1))
        count=1
        remove_c=[]
        temp=d.get(h)
        for i in range(0,len(posi_l),2):
            #print posi_l
            start=int(posi_l[i])+1 # How to set posi can reference ReadMe and ReadMe1
            end=int(posi_l[i+1])-1
            if float(end-start+1)/float(c_len) > my_thr:
                print h+"start end %d %d" % (start,end)
                new_c=temp[start-1:end]
                new_h=h.split(':')[0]+'_New_'+str(count)+':'+str(len(new_c))
                d[new_h]=new_c;d1[new_h]=new_c
                remove_c.append(new_c)
                count+=1
        for row in remove_c:
            temp=temp.replace(row,'-Contig-')
        fw1.write(h+'\n');fw1.write(temp+'\n')
        d.pop(h);d1.pop(h)
f.close();fw1.close()
#fw=open(current_p+'/R2_All_Contigs.fa','w')
#for key in d.keys():
#    fw.write(key+'\n')
#    fw.write(d.get(key)+'\n')
#fw.close()
if os.path.exists(current_p+'/R1_Contigs_E_T.fa'):os.remove(current_p+'/R1_Contigs_E_T.fa')
fw=open(current_p+'/Contigs_1.fa','w')
for key in d1.keys():
    fw.write(key+'\n')
    fw.write(d1.get(key)+'\n')
fw.close()
fw=open(current_p+'/R2_Contigs.fa','w')
for key in d1.keys():
    fw.write(key+'\n')
    fw.write(d1.get(key)+'\n')
fw.close()
#20120410
f=open(current_p+'/Contigs_1.fa');fw=open(current_p+'/CISA2_thr.out','w')
w_g=0.0;contig=0
for i in f:
    if '>' in i:contig+=1
    else: w_g+=len(i.replace('\n',''))
fw.write("%.2f"%(w_g/contig)+'\n')
fw.write("WholeGenome:"+str(w_g)+'\n')
fw.write("#Contigs:"+str(contig)+'\n')
f.close();fw.close()
#20120410


