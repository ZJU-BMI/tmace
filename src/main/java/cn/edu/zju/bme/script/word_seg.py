import os
import jieba

with open("../../../../../../../../resources/del_word.txt", 'r', encoding='utf-8') as f:
    for line in f:
        jieba.del_word(line)

jieba.load_userdict("../../../../../../../../resources/user_dict.txt")
jieba.load_userdict("../../../../../../../../resources/unit.txt")
jieba.load_userdict("../../../../../../../../resources/people.txt")

def files(path):
    for file in os.listdir(path):
        if os.path.isfile(os.path.join(path, file)):
            yield file

root = '../../../../../../../../resources/sentences'
save = '../../../../../../../../resources/segmented'

def process():
    for f in files(root):
        with open(os.path.join(root, f), 'r', encoding='utf-8') as rf:
            path = os.path.join(save, f)
            with open(path, 'w', encoding='utf-8', newline='\n') as wf:
                for line in rf:
                    out = " ".join(jieba.cut(line))
                    wf.write(out)


def processOne(fileName):
    with open(os.path.join(root, fileName), 'r', encoding='utf-8') as f:
        path = os.path.join(save, fileName)
        with open(path, 'w', encoding='utf-8', newline='\n') as wf:
            for line in f:
                out = " ".join(jieba.cut(line))
                wf.write(out)


if __name__ == "__main__":
    process()
