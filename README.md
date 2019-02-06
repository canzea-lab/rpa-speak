

# Getting Started


http://20.20.20.20:5050/read?voiceId=Amy&text=Thank%20you%20sir&outputFormat=pcm



# Building

docker build --tag rpa-speak .


docker run $MODE -p 5050:5050 --name=rpa-speak -v /Users/aidancope/.aws:/root/.aws rpa-speak
