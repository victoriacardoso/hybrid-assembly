import commands
import re
import sys
path=sys.argv[1]
count=sys.argv[2]
threshold=int(sys.argv[3])
current_p=sys.argv[4]
makeblastdb=sys.argv[5]
blastn=sys.argv[6]
d=dict() #big dict()

def run_Blast():
    global count
    print 'Ref'+str(count)+'.fa and Ref'+count+'.fa start to run Blast!'
    (s,o)=commands.getstatusoutput(makeblastdb+' -in '+current_p+'/Contigs_'+count+'.fa -dbtype nucl -parse_seqids -out '+path+'/DB')
    (s,o)=commands.getstatusoutput(blastn+' -query '+current_p+'/Contigs_'+count+'.fa -db '+path+'/DB -out '+path+'/result_blast.txt -word_size 28 -reward 1 -penalty -2 -outfmt \"7 qseqid sseqid qstart qend sstart send length evalue score\"')
    #(s,o)=commands.getstatusoutput("/opt/MUMmer3.22/nucmer -o -p "+path+"/result"+str(count)+" "+path+"/Ref"+str(count)+".fa "+path+"/Query"+str(count)+".fa")
    print "Done!"
def make_new_result(threshold):   # Remove map which align_len/contig_len < 0.3
    f=open(path+'/result_blast.txt')
    #fw=open(path+'/n_result_blast.txt','w')
    while True:
        while True:
            s=f.readline()
            if not s:break
            if 'hits found' in s:break
        if not s:break
        try:
            hits=int(s.split()[1])
        except ValueError:
            print 'She needs a number but you give ' + s.split()[1]
        #if type(hits)!=int:print 'This number is not int!';break    
        hits_list=[]
        for i in range(hits): # sometimes, hits number isn't exact
            s=f.readline()
            if '# ' in s :break 
            qid=s.split()[0];sid=s.split()[1]
            if qid==sid:continue
            align_len=float(s.split()[-3])
            q_len=float(s.split()[0].split(':')[1])
            s_len=float(s.split()[1].split(':')[1])
            if align_len <= threshold : continue
            hits_list.append(s)
        if len(hits_list)==0:continue
        find_head_tail(hits_list,q_len,qid)
    f.close();#fw.close()
def find_head_tail(hits_list,q_len,qid):  # Capture head and tail info from coords to write into Wait2Process.txt
    global f_list
    head=True;tail=True;h_list=[];t_list=[];t_h=1;t_t=int(q_len)
    while True:
        if t_t<1 and t_h> int(q_len):
            h_list.append('#none');t_list.append('#none')
            break
        key_h=str(t_h)
        key_t=str(t_t)
        for data in hits_list:
            qstart=data.split()[2]
            qend=data.split()[3]
            if head:
                if key_h == qstart:h_list.append(data)
            if tail:
                if key_t == qend:t_list.append(data)            
        if len(h_list)>0:head=False
        if len(t_list)>0:tail=False
        if not head and not tail:break
        t_h+=1;t_t-=1
    f_list.write('>'+qid+'\n')
    for row in h_list:
        f_list.write(row)
    for row in t_list:
        if row in h_list:continue
        f_list.write(row)
    f_list.write('#'+'\n')

f_list=open(path+'/Wait2Process.txt','w')
run_Blast()
make_new_result(threshold);
f_list.close()

