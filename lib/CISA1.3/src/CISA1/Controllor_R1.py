import commands
import os
import filecmp
from Auto_Run import Get_Info_NUCMer
class R1:
    def Extend(self,infile,CISA_S,current_p):
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA1/Filter1.py "+current_p)
        print o
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA1/Extend.py "+infile+" "+current_p)
        print o
    def Trim(self,CISA_S,current_p):
        (s,o)=commands.getstatusoutput("python "+CISA_S+"/CISA1/Trim.py "+current_p)
        print o
    def Get_explained_info(self,infile,current_p):
        f=open(current_p+'/CISA1/explained.txt');contig=open(infile)
        my_dict=dict()
        while True:
            h=contig.readline().replace('\n','')
            if not h:break
            s=contig.readline().replace('\n','')
            my_dict[h]=s
        totals=0
        for s in my_dict.values():
            totals+=len(s)
        for i in f:
            tmp=i.replace('\n','').split()
            for j in tmp:
                if my_dict.get('>'+j):my_dict.pop('>'+j)
        f.close();contig.close()
        rests=0
        for s in my_dict.values():
            rests+=len(s)
        fw=open(current_p+'/info','a')
        fw.write('Total Base:'+str(totals)+'\n')
        fw.write('Rest Base:'+str(rests)+'\n')
        fw.write('Ratio:%3.2f' % ( (float(rests)/float(totals)) *100) +'% \n')
        print 'Rest:'+str(rests)
        print 'Total:'+str(totals)
        print float(rests)/float(totals)
    def Start(self,genome_l,infile,nucmer,CISA_S,current_p):
        print 'CISA1....'
        mywork=Get_Info_NUCMer()
        mywork.Start(genome_l,infile,nucmer)
        self.Extend(infile,CISA_S,current_p)
        self.Trim(CISA_S,current_p)
        self.Get_explained_info(infile,current_p)
        print 'CISA1 Done!'
