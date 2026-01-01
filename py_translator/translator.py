import googletrans
import json
import asyncio
translator = googletrans.Translator()
default_path = "../src/main/resources/assets/epicsorigins/lang/"
languages = [
    ["de_de.json", "de"],
    ["es_ar.json", "es"],
    ["es_cl.json", "es"],
    ["es_ec.json", "es"],
    ["es_es.json", "es"],
    ["es_mx.json", "es"],
    ["es_uy.json", "es"],
    ["es_ve.json", "es"],
    ["fi_fi.json", "fi"],
    ["fr_fr.json", "fr"],
    ["he_il.json", "he"],
    ["id_id.json", "id"],
    ["it_it.json", "it"],
    ["ja_jp.json", "ja"],
    ["ko_kr.json", "ko"],
    ["nl_nl.json", "nl"],
    ["pl_pl.json", "pl"],
    ["pt_br.json", "pt"],
    ["ro_ro.json", "ro"],
    ["tr_tr.json", "tr"],
    ["uk_ua.json", "uk"],
    ["zh_cn.json", "zh-cn"]
]
# for lang in sorted(googletrans.LANGUAGES):
#     print(f'{lang}: {googletrans.LANGUAGES[lang]}')

async def translate():
    with open(default_path+"en_us.json", 'r', encoding='utf-8') as f:
        translations = json.load(f)
        for lang in languages:
            new_translations = translations.copy()
            print(lang[1])
            for translation in translations:
                new_translation = await translator.translate(translations[translation], dest=lang[1])
                new_translations[translation] = new_translation.text
                # print(f'{translation}: {new_translation.text}')
            json.dump(new_translations, open(default_path+lang[0], 'w', encoding='utf-8'), indent='\n  ', ensure_ascii=False)

asyncio.run(translate())