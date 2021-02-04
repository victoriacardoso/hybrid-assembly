import commands
import os
import filecmp
class R2:
    def SplitFile(self,CISA_S):
        f=open('R1_Contigs.fa')
        count=1
        while True:
            h=f.readline()
            if not h:break
            seq=f.readline()
            fw=open('CISA2/Ref_'+str(count)+'.fa','w')
            fw.write(h)
            fw.write(seq)
            fw.close()
            count+=1
        f.close()
    def run_Nucmer(self,count,nucmer):
        print 'Ref_'+str(count)+'.fa and R1_Contigs_E_T.fa will be started to run NUCmer!' # 20120411
        (s,o)=commands.getstatusoutput(nucmer+" --maxmatch -o -p CISA2/result"+str(count)+" CISA2/Ref_"+str(count)+".fa R1_Contigs_E_T.fa")
        print "Done!"
    def Run_N(self,nucmer):
        f=open('info')
        try:
            end=int(f.readline().replace('\n','').split(':')[1])
        except ValueError:
            print 'Needs a correct info!'
        f.close()
        print 'Range from 1 to '+str(end-1)
        for i in range(1,end):
            count=i
            self.run_Nucmer(count,nucmer)
        return end
    def Process_Gap(self,r2_gap,CISA_S,current_p):
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA2/Parser_All.py "+r2_gap+" "+current_p)
        print o
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA2/Process_gap.py "+current_p)
        print o
    def Start(self,nucmer,r2_gap,CISA_S,current_p):
        print 'CISA2...'
        self.SplitFile(CISA_S)
        end=self.Run_N(nucmer)
        self.Process_Gap(r2_gap,CISA_S,current_p)
        for i in range(1,end):
            if os.path.exists('CISA2/result'+str(i)+'.coords'):os.remove('CISA2/result'+str(i)+'.coords')
            if os.path.exists('CISA2/result'+str(i)+'.delta'):os.remove('CISA2/result'+str(i)+'.delta')
            if os.path.exists('CISA2/Ref_'+str(i)+'.fa'):os.remove('CISA2/Ref_'+str(i)+'.fa')
        print 'CISA2 Done!'
