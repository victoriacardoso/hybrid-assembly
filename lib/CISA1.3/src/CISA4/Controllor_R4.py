import commands
import os
import filecmp
class R4:
    def run_Blast(self,thr,count,CISA_S,current_p,makeblastdb,blastn):
        print 'Auto_Run! '+count
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA4/Auto_Run.py "+current_p+"/CISA4/Round"+count+" "+count+" "+thr+" "+current_p+" "+makeblastdb+" "+blastn)
        print "Done!";print o
    def run_Filter(self,count,CISA_S,current_p):
        print 'Filter1! '+count
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA4/Filter1.py "+current_p+"/CISA4/Round"+count)
        print "Done!";print o
    def run_C_R(self,count,CISA_S,current_p):
        print 'Connect_Remove.py! '+count
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA4/Connect_Remove.py "+current_p+"/CISA4/Round"+count+" "+count+" "+current_p)
        print "Done!";print o
    def run_Trim(self,count,CISA_S,current_p):
        print 'Trim.py! '+count
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA4/Trim.py "+current_p+"/CISA4/Round"+count+" "+count+" "+current_p)
        print "Done!";print o
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
        
    def Start(self,thr,count,CISA_S,current_p,makeblastdb,blastn):
        print 'CISA4...'
        while True:
            size=0;size1=0
            if not os.path.exists('CISA4/Round'+count):
                os.makedirs('CISA4/Round'+count)
            self.run_Blast(thr,count,CISA_S,current_p,makeblastdb,blastn)
            self.run_Filter(count,CISA_S,current_p)
            self.run_C_R(count,CISA_S,current_p)
            self.run_Trim(count,CISA_S,current_p)
            size=os.path.getsize('Contigs_'+count+'.fa')
            size1=os.path.getsize('Contigs_'+str(int(count)+1)+'.fa')
            if size == size1:
                f=open('info2','w')
                f.write('Round4_result='+str(int(count)+1)+'\n')
                f.close()
                self.N50('Contigs_'+str(int(count)+1)+'.fa')
                break    
            count=str(int(count)+1)
        print 'CISA4 Done!'

