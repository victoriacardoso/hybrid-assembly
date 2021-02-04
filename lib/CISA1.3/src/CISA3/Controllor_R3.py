import commands
import os
import filecmp
class R3:
    def run_Blast(self,count,CISA_S,current_p,makeblastdb,blastn,my_thr):
        print 'Auto_Run! '+str(count)
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA3/Auto_Run.py "+current_p+"/CISA3/Round"+str(count)+" "+str(count)+" "+current_p+" "+makeblastdb+" "+blastn+" "+my_thr)
        print o
        print "Done!"
    def run_Filter(self,count,CISA_S,current_p,my_thr):#20120410
        print 'Filter1! '+str(count)
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA3/Filter1.py "+current_p+"/CISA3/Round"+str(count)+" "+my_thr)#20120410
        print o
        print "Done!"
    def run_C_R(self,count,CISA_S,current_p,my_thr):#20120410
        print 'Connect_Remove.py! '+str(count)
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA3/Connect_Remove.py "+current_p+"/CISA3/Round"+str(count)+" "+str(count)+" "+current_p+" "+my_thr)
        print o
        print "Done!"
    def run_Trim(self,count,CISA_S,current_p,my_thr):#20120410
        print 'Trim.py! '+str(count)
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA3/Trim.py "+current_p+"/CISA3/Round"+str(count)+" "+str(count)+" "+current_p+" "+my_thr)
        print o
        print "Done!"
    def get_repeat_data(self,count):
        fr=open("CISA3/Round"+str(count)+"/Repeat_Region.txt")
        repeat_v=0
        for data in fr:
            if ('>' in data) or ('#' in data):continue
            temp_v=int(data.split()[-3])
            if temp_v>repeat_v:repeat_v=temp_v    
        fr.close()
        return repeat_v
    def N50(self,ff):
        f=open(ff)
        d=dict()
        genome=0
        while True:
            h=f.readline()
            if not h:break
            seq=f.readline().replace('\n','')
            d[h]=len(seq)
        f.close()
        d_s=sorted(d.values(),key=lambda x:x,reverse=True)
        for value in d_s:
            genome+=value
        print 'whole:%d'%genome
        n5=genome/2
        genome=0
        for row in d_s:
            genome+=row
            if genome > n5:
                print 'N50: %d' % row
                break
        print 'Number of contigs: %d'%len(d_s)
        print 'Length of the longest contig: %d'%d_s[0]
    
    def Start(self,CISA_S,current_p,makeblastdb,blastn):
        print 'CISA3...'
        #20120410  Get thr
        my_f=open(current_p+'/CISA2_thr.out')
        my_thr=my_f.readline().replace('\n','')
        my_f.close
        #20120410
        count=1
        repeat_v=0
        while True:
            size=0;size1=0
            if not os.path.exists('CISA3/Round'+str(count)):
                os.makedirs('CISA3/Round'+str(count))
            self.run_Blast(count,CISA_S,current_p,makeblastdb,blastn,my_thr)
            self.run_Filter(count,CISA_S,current_p,my_thr)#20120410
            self.run_C_R(count,CISA_S,current_p,my_thr)#20120410
            self.run_Trim(count,CISA_S,current_p,my_thr)#20120410
            #tmp_r_v=0
            #tmp_r_v=self.get_repeat_data(count)#20120419
            #if tmp_r_v:repeat_v=tmp_r_v#20120419
            size=os.path.getsize('Contigs_'+str(count)+'.fa')
            size1=os.path.getsize('Contigs_'+str(count+1)+'.fa')
            if size == size1:
                repeat_v=self.get_repeat_data(count)
                f=open('info1','w')
                f.write('Round3_result='+str(count+1)+'\n')
                f.write('Repeat_region='+str(repeat_v)+'\n')
                f.close()
                self.N50('Contigs_'+str(count+1)+'.fa')
                break    
            count+=1
        print 'CISA3 Done!'

