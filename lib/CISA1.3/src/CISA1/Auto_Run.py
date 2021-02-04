import commands
import re
import os
d=dict() #big dict()
max_v=0;max_h="";max_s="";count=0;h_list=[];t_list=[]
class Get_Info_NUCMer:
    def export_ref(self,count): # find the longest contig as Ref seq
        global max_v,max_h,max_s,d
        fw=open('CISA1/Ref'+str(count)+'.fa','w')
        for key in d.keys():
            seq=d.get(key)
            if len(seq)>max_v:
                max_v=len(seq.strip())
                max_h=key
                max_s=seq
        d.pop(max_h)
        fw.write(max_h)
        fw.write(max_s)
    def export_query(self,count):  # Remove contigs which are used in last Query.fa to form new Query.fa
        global d
        fw=open('CISA1/Query'+str(count)+'.fa','w')
        for key in d.keys():
            fw.write(key)
            fw.write(d.get(key))
        fw.close()
    def modify_dict(self):   # Remove contigs which are used in last Query.fa to form new Query.fa. Use a Dict object to control.
        global count,d,max_h
        fmap=open('CISA1/result'+str(count-1)+'.coords')
        for i in range(5):
            s=fmap.readline()
        h_set=set()    # if the queryID of which score >0.8, put it in the set()
        score_d=dict() # if the queryID's totally score < 0.8, it can attend next round.

        isHEADorTAIL=dict() # Jduge the query maps to the Ref in head, tail or middle. #20120406
        ref_s=1;ref_e=0;my_refID=''
        while True:
            s=fmap.readline()
            if not s:break
            #20120410 if identity < 95, keep it to next round
            idy=float(s.split()[-4])
            if idy < 95.0 :continue
            #20120410
            ref_e=s.split(':')[1].split()[0]
            temp=s.split('\t')[1].replace('\n','')
            align_len=float(re.sub(' +',' ',s).split('|')[2].split(' ')[2])
            contig_len=float(s.split(':')[2].replace('\n',''))
            #20120406
            ref_mapped_s=s.split()[0]
            ref_mapped_e=s.split()[1]
            if ref_mapped_s=='1' or ref_mapped_e==ref_e: isHEADorTAIL[temp]=True
            else: isHEADorTAIL[temp]=False
            my_refID=s.split('\t')[0].split('|')[-1]
            temp_s=score_d.get(temp)
            if temp_s:
                temp_s+=(align_len/contig_len)
                score_d[temp]=temp_s
            else:
                score_d[temp]=(align_len/contig_len)
        for key in score_d.keys():
            if isHEADorTAIL.get(key):#20120406
                if (score_d.get(key))<0.8:continue  # if it is in head or tail and <0.8 , keep it to attend next round
            else:
                if (score_d.get(key))<0.95:continue  # if it is in middle and <0.95 , keep it to attend next round
            h_set.add(key)                      # otherwise, put it into set(), and then remove it from "big dict()"
        #20120406
        if my_refID and h_set:
            f_ex=open('CISA1/explained.txt','a')
            f_ex.write(my_refID+'\n')
            for myid in h_set:
                f_ex.write(myid+'\t')
            f_ex.write('\n')
            f_ex.close()
            
        for key in d.keys():
            seq=d.get(key)
            temph=key.split(' ')[0].replace('>','').replace('\n','')
            if temph in h_set:d.pop(key)
            #if temph == max_h.replace('>','').replace('\n',''):d.pop(key)
        fmap.close()
        if os.path.exists('CISA1/result'+str(count-1)+'.coords'):os.remove('CISA1/result'+str(count-1)+'.coords')
    def run_Nucmer(self,count,nucmer):
        print 'Ref'+str(count)+'.fa and Query'+str(count)+'.fa start to run NUCmer!'
        (s,o)=commands.getstatusoutput(nucmer+" -o -p CISA1/result"+str(count)+" CISA1/Ref"+str(count)+".fa CISA1/Query"+str(count)+".fa")
        print "Done!"
    def make_new_coords(self):   # Remove map which align_len/contig_len < 0.8 and identity < 95
        global count
        f=open('CISA1/result'+str(count)+'.coords')
        fw=open('CISA1/new_coords/n_result'+str(count)+'.coords','w')
        for i in range(5):
            s=f.readline()
        while True:
            s=f.readline()
            if not s:break
            #20120410 if identity < 95, keep it to next round
            idy=float(s.split()[-4])
            if idy < 95.0 :continue
            #20120410
            align_len=float(re.sub(' +',' ',s).split('|')[2].split(' ')[2])
            contig_len=float(s.split(':')[2].replace('\n',''))
            if (align_len/contig_len)<0.8:continue
            fw.write(s)
        f.close();fw.close()
    def find_head_tail(self):  # Capture head and tail info from coords to write into Wait2Process.txt
        global count,d,max_v,max_h,max_s,h_list,t_list
        head=False;tail=False;h_list=[];t_list=[];t_h=1;t_t=int(len(max_s.replace('\n','')))
        while True:
            f=open('CISA1/new_coords/n_result'+str(count)+'.coords')
            #Does file have alignment data?
            s=f.readline()
            if not s:
                f.close()
                h_list.append("none")
                t_list.append("none")
                break
            else:
                f.close()
                f=open('CISA1/new_coords/n_result'+str(count)+'.coords')
            key_h=' '+str(t_h)+' '
            key_t=' '+str(t_t)+' '
            while True:
                s=f.readline()
                if not s:break
                key_range=s.split('|')[0]
                if not head:
                    if key_h in key_range:h_list.append(s)
                if not tail:
                    if key_t in key_range:t_list.append(s)            
            if len(h_list)>0:head=True
            if len(t_list)>0:tail=True
            if head and tail:break
            t_h+=1;t_t-=1
            f.close()
        if os.path.exists('CISA1/new_coords/n_result'+str(count)+'.coords'):os.remove('CISA1/new_coords/n_result'+str(count)+'.coords')
    def out_data(self,count):
        f_out=open('info','w')
        f_out.write('Total:'+str(count+1)+'\n')
        f_out.close()

    def to_del_file(self,count):
        if os.path.exists('CISA1/Ref'+str(count)+'.fa'):os.remove('CISA1/Ref'+str(count)+'.fa')
        if os.path.exists('CISA1/Query'+str(count)+'.fa'):os.remove('CISA1/Query'+str(count)+'.fa')
        if os.path.exists('CISA1/result'+str(count)+'.delta'):os.remove('CISA1/result'+str(count)+'.delta')
        #if os.path.exists('CISA1/result'+str(count)+'.coords'):os.remove('CISA1/result'+str(count)+'.coords')
    def Start(self,genome_l,infile,nucmer):
        global d,count,max_v,max_h,max_s
        f=open(infile)
        while True:
            h=f.readline()
            if not h:break
            seq=f.readline()
            d[h]=seq
        f.close()
        if not os.path.exists('CISA1/new_coords'):
            os.makedirs('CISA1/new_coords')
        whole_g=0
        breakpoint=float(genome_l) *1.1
        f_list=open('CISA1/Wait2Process.txt','w')
        while True:
            #before modify dict, dict have ctg more than 3
            if len(d.keys())<3:self.out_data(count);break  #  if amount of rest of contigs is < 3 , break
            if whole_g > breakpoint :
                print "Whole_Genome: "+str(whole_g)
                print "Break!"
                self.out_data(count)
                break # if whole genome's length > 4637712  , break

            #modify_dict func => to remove explained ctg
            if count>0:self.modify_dict()
            print "Contigs: "+str(len(d.keys()))
            print "Whole_Genome: "+str(whole_g)

            #20130304
            if len(d.keys())<3:self.out_data(count);break
            #after modify dict(remove explained ctg), dict have ctg more than 3
            
            max_v=0;max_h="";max_s="";
            self.export_ref(count)
            self.export_query(count)
            self.run_Nucmer(count,nucmer)
            self.make_new_coords()
            self.find_head_tail()
            self.to_del_file(count)
            print "Writing..."
            f_list.write(max_h.replace('\n','')+','+str(count)+'\n')
            #f_list.write(max_s)
            for row in h_list:
                temp=re.sub(' +',' ',row)
                if temp[0]==' ':temp=temp.replace(' ','',1)
                f_list.write(temp)
            for row in t_list:
                temp=re.sub(' +',' ',row)
                if temp[0]==' ':temp=temp.replace(' ','',1)
                f_list.write(temp)
            f_list.write('#'+'\n')
            whole_g+=max_v
            #max_v=0;max_h="";max_s="";
            count+=1
        f_list.close()
        if os.path.exists('CISA1/new_coords'):os.rmdir('CISA1/new_coords')

        #oliver 20140304 add choice when few ref ctg.
        if whole_g < breakpoint:
            while True:
                print '(%d) representative contigs were selected to contain (%d) bp, but fewer than 3 contigs were left. CISA1 will not select additional representative contigs, do you want to jump to CISA2?(y/n, default: n)'%(count,whole_g)
                choice ='y'
                if choice == 'y' or choice == 'yes':
                    break
                if choice == 'n' or choice == 'no' or choice=='':
                    print choice
                    exit(0)
