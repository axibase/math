package com.axibase.statistics;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.axibase.statistics.SquareRootDev.estimateSqrtLelieveld;
import static com.axibase.statistics.SquareRootDev.estimateSqrt;

/**
 * test
 */
public class SquareRootDevTest {

    String[] strings = {"0",
            "1",
            "4",
            "9",
            "10000000000000000", // 10^16
            "8100000000000000000000", // 81 * 10^20
            "121000000000000000000000000000000", // 121 * 10^30
            "1210000000000000000000000000000000000000000", // 121 * 10^40
            "1543456953465123654037645628562864375123654356534629365923",
            "44242425252526374885450349876045867765309458673490586749058",
            "63653265376734672638726425348267354892567421543862737687312564624358367462645392348354245613547126534",
            "636532653767346726387264253482673548925674215438627376873125646243583674626453923483542456135471265323",
            "0.01",
            "0.0004",
            "0.000000000000000000000121", // 121 * 10^(-24)
            "2",
            "3",
            "12345678901234567890",
            "0.34569876524342561618732367285",
            "0.0000000010000001001010000201021020201002312102012001021032024332140235412343401223423435400012345",
            "100",
            "0.352818528921979802147666515426902895727765101266038472739273675725801740461899203391467404999652538893639944052164993861983322930689421050680216977023059231670566602635420201308103469059177274411042449967235640170639404861525798685782654348576008053906678123883330032297238500985193573602497626771580945594770502324168762166567267289269665176402913948019856635315861428993880073282624381118485716289019571647659739495016074896218885978289930340857274515862259731833170589692399895634244147482703269890913247408478923447178902017779845443742308844558419999046111467812933248023432427998259994182648578288775189490102071574925542172618705808377981407075661662506742408661232720032172324989308087757531861080580705851938971046656424285025030048776692043487994681169121806548569325273977674268512834848319197541770346893931298171263359209287656256258646644947830231869419567842967424731270811652666974200603722897904972781881580215186731799243387581020765695927222985513092811072096813507351486163616474354491678516423019462856092324589467805417576434369118271125610599862858091034066115393896376402586115322883185629500789989825539099146963464597667690086645833677773341477425089421202029845828438186267290619982627882863531585505515611951359679835295645460495124946561810293085634756353824263832486284294130150330118760419800451287490407783325022093281030504666007839427958453674663221723404057631681719148427415412519993998928227188905950986505087130321733361332156307945821264095564986339643413952656671077302618008809419170629325044973291573447163149540989060334863465155227701549160360633076458829101325288181727254148455952801479839414624838237422509188727938777531618602609770842427633605357541405468766390741255444162263154213109493016536875507122768177992567211419631697956177013676147279765005362827247748175113476912061656596560057107219180031161086243871513570693257886733246854019430780326947936432096164343749503593404634630874853900858530925398333908462443249715617740821018862850469001766145798985952338158907879181848149273610535411146431164860190045987407079776737665145855618269772150097545656533084155333408671462357567952863327464733354130776616979840611935007402670571489089668548000566788250160846791012993775567439582182725205696062583108329707566149812378805220075179590531688600283608991116551549027626948019331454835003314143801424170814628183200055052199722542207640173290280989264977286415610857154441019808138140819856280410105031134448183196991569236564590128053475070955652666628369465344771805924916499026673246861918192823814334395024295138823232784709065496229159508579419100068387896656615250289291758465598666278370085239497533286365488975303055116815329102108942419789159408743304837147769168826679563309806682084876882871097306440805958691670681692306549364491345283413772350464609125873897690388407474850326909999101415928102266360806164931776847358063319741306986987628745641280473524000212369792965384091797156403233777795567784543089119411659393168440334248938741623001128517522693879403929699173451781412798671665283558729628243926890969910443705973489192155576353499381779194610671587110478486168711282156757950565546363036019684717556713624143788093810345688751549193665721896867887651320967000222128167164946865556889602661431879227060777814881492519547779448310707438750917861990561928144098861424157634058497094462991766917173509132343518475084202673630176061598854141914185610767706802039701642072360625842378688027735898240182190514393189209471611768527100950310316851880853478199424580668628085693182998731100236819825441203790704941193496754099933586278887732102406442366897274992111227286780538771607254703391727435908633086669314433562019482301856480278389899972125503889024035852303562592479624352423337002906871575935923566494087298753092343486998023539344694510533797805958900582731852193990439519387549384829431113980579322708657363957164395621754828226758348067425530114629280261288667931574298350041682965467047588359489270073143306999546737192711175822029705263447027865758551542802171488905955004349464501480093234324812778987292165459441941026474022142289145222953357220372418411212413061286574450794826437795190069128780886322873705075391534786287589843850190532006721823083493988784719966839685662262264057458858102263578441838880872379410128677308321014897665822450598742323482617706556668314972948257303337118691806018319646689269303327895611192483290610412447084434138915732180208661257866451990865563784573699574180113438083551175839547069343207045045995100912147324966281922502715105823762488576030121131547913526483123729519773892343318565326104868289395276455971388735259382226159413912984029551781738507760341551002029698244452208252501876382885200201519676863857276544629621043623553022169433277290829018245988763416544714675486727259277158207185683059120469903788050023125005340124434545488528442744313808998213896416775571508334831773577301265300131926124116720552814768332474881127016239632373827247236037212368475176197284063580380941316779730166327775936730580436889438218610438044839337499052077613183040131513844261362138421340482537949355008184657331131802878928188016227607049663793923496957161690184263051610305144160357410316194354127288952746801158072255627528356254285419729629914150135164293325498786148924752199146507320087447951639563162872093511891508859874602833528357633853230657750414976922360211994756093221208726220406868776436423802282853111630343720137345935295997001814433021729906260871559252222785750795910525236903608970515342971450150471140143424730302419832493045288822449089904121958127502411554916890000523296881346660246083743133530717406880789993705668249624474982096681744820733458578119329965309671669003956057605867177163829210966938191726511853645119095575338547037668182994797666943720389432824576727544324133629219070818056301690488891414444637139445286226789026200554535161974614349396819623126119487492383584029405209572120869911038247087356343641894799034464020392552038747499914221074709047988903494270079315963932888035593068602395612879290719088991015317352105164692480211676756550307172096101637696730714405029471608453893636023178565563010040550935381304155988922549067016051209896944367217070301142516901257757847663657027699473130521129513917273661361656490890291200300878354926042615514456147252662943337364841398596952462369372282916304134920158141647180675038504459880237319",
            "0.3704414496147458043239009395617047944086858144119878924185756885634465178265639957436682608992850431528875772910438961767789660956338981163485821044637730910338154465350724189866982683311555671620052485795658115332731653809349086127884142502541496887419121294054900830869572483571546755221875456624182011489210014751262928598607034920679348286675516831920287240539229451188424580188154364219776026631431350586879415459341115561387396945979966832908224045816869121347271692713781045541081443569070566111295116088646584493393135015126480368225833019747734066098558811220163859079744910353123435798892520065563915099366278357329819968875534096196424171274291720561778658555516743418082573307466382863456381706523938896232146038951175323055847108477243786596895798611531668926391650940182055354088197070307927273234737822603982038281719925414132758616395812464579084153938322333658650022679941819166883274602994829463569507510621628677947809394629461223513376025621817599351574191110652821935911482138676717098981927883536001893478680568151353641010418476947919470356547567688483250287703537721593505566280595668177280269889383901895105255895376621751734725860969063914406464991878408220481405741439202496652228269171920153982747017479089104843570070039607256062214861612362314215879462187413119849378965802131467420813044542020122838203602682596750883408910179987764677326702286161643709878594235320882144419763067752074103473182485086035007252795075878243569819113185967281189957260133497597432630277213722494179834728003286415588325698300562095260387261345758133251300416554752278539980265184486241826347804190819139198434906375866114163222349276929609379374489611730383131017748133135086250131143368180760641413214814247525647962962506796013510694562013125379951971414669700960685192978617491295422905741884232100518460483132775402781735919983376290322984293875789186760639098703846685218807794857751486752445932899925858917792981090482309847437855118296587049164134033342571044675743512864862265045738739478728498363497746177824629996982580046839222309545744836760022558620283605005937010444065784283501954223242625986431602553113764109353851826962117239799217339808246594580557273967871699649364499876774403047142845386848784428695199449277453033493640120085711302851121002659901673961433378854767798144923658927298695261639158713147395001273508824635128714358452512855411362752528575758666266955481161913329356689236842844149379643217878524180197703859033412069505366083310295037175373976292336507395086373892577996764656761534283965470594611877356340945783211374689582555342944385914249620062712877550542215951059623229852898842373076914136133011956700928002947133117838413770428793221801300294466985508034713884662830447231681213213453088249946387085521135423767166089095463791582798943262281462603511049649448602038909640749221743357739284214518642067744698207719784254840163735688681220143447169066417397850513152151172925678941113211511488294517490818509799257077217860813987272636878917290537649587894143926934617327030630464335201637474803973803481312937483071227383978565095581597187146017304178636152546822867980514246046867891205140681989128187926398965522152115287878938650164073837276916834107192828306279151867321361640588224589583682064104703310151296518312356773750488490614378074063817722262451733489708197825519148847644417701750844464075378761741932957451293851080807438780643292507999652894923625303388218504724524101475926383911444970314259869762536377894249479600964504005194841276545727943964180221541021551893608412045225950918622670285130250939114627360832236218758446714342169523550828526750071035101377933591762570951073759796648090901063782485486430620813838112411821267131738006814919543240748260381298765451386562881502096368910683149659502189914232150893699775539071683903660941461710806704334379721414595601375795373823088600902817159985420227019117491114505750593023953380614180364225427887230075091220332635015220403015814921384764609328078231400474026084129630639127780300095505205565214379908943243985784295494870330194989698573112838337394486897873777987693959322122249282198729242956655269908815454983009179273491006503173264834771675220189839276984816531397782302714545056026571070747493645541470730150284469644555110460217511766412192278784851524472840460723407972397770470506724981642554587090814966702157837472015930850696313064125245272711386944443685326283547567833272116891416183983492498165027966141658486231277038291238232042381665866600838015401010752590930877381538542505995334840994987436993943846881555668500120151301222339344767520929180141479087574450709914651576426916284089056673887767679182155401008181532163593602610710533123482464289972481762283847508395724985406150340372423356858656908981870434723385748290770386823882656387018104738982523779708078373435549430264549248276330960229680763403704851925115514694750263716400323599540673081863137795340302970353034811014077387115126143502241601874450023739225894190965343624419802662416123938010357149155155158070205385908001424068015672541765333187757144777329733806626943338407376567875563654225720791166753403298624936534205638145490496775992436092699623809305521548181425043473124182177286348020244155764980570203077889947798650165448260538637303825741674839452517138336502169515215234353045429102211043170378182253253700894234916634936708405538662859690371558893741652930582679933570155643793424166440120363764393362178255382474838783179070253298089056144148374657724173106434660191218290915825157305723999720122974445055516475756360795234550515097769418164314771164330988205900875835454703158213941916524949822063279665754210276182063895153131894256731366049873786157074971662895841422388915796443007345622069435435494809677156351622169430855493009270902391003864421963694023191727026429587251273169496908412981135421024974614980490578123003230457916247639843686358271350248898900746970772790961236509115787402927389642030179791400461144975720022545501870757237403414088511411787030002169963028032603713881756920936705390746081150881998332716120522410740414107715653414806387043298265957577938112878348853598032507082835361497175029896706040958261676214783873229877093287701178082573924587762325188337319719972859185790612097495670480685224304039483649568298950629082634279974513030697960696338555261086002393498345916654116043596395211269642118589458231325425622536782519593819626204481338024693192348664944690540884045520377539929400690230813532778382725499114360925077096890106341923750489304140926091770100881358947935869323446864760644872941093902757494795805073789426926868406712143324440699703701925726007992564038124603557340171691696678521360643709341850600144313199244686931746510017782415385775036942210620630668945410982446919595240144332547495632321353108974554875210048356275281331571955831162671133692756099203611801337534044299587626014500550470492131759253734557805298953083287036883809464409389995524632758338366357842904383395394199249337719451705123941850315814334696598081714863712447856021691501017392515"
    };


    BigDecimal[] decimals = new BigDecimal[strings.length];

    @Before
    public void init() {
        for (int i = 0; i < strings.length; i++) {
            decimals[i] = new BigDecimal(strings[i]);
        }
    }

    @Test
    public void testEstimateSqrtFixed() {
        for (int i = 0; i < strings.length; i++) {
            System.out.println("\n--------------------------------");
            System.out.println("Number:  " + strings[i]);

            BigDecimal myEstimation = estimateSqrt(decimals[i]);
            BigDecimal myError = decimals[i].subtract(myEstimation.multiply(myEstimation));
            System.out.println("My error:    " + myError.toPlainString());

            BigDecimal fransEstimation = estimateSqrtLelieveld(decimals[i]);
            BigDecimal fransDeviation = decimals[i].subtract(fransEstimation.multiply(fransEstimation));
            System.out.println("Frans error: " + fransDeviation.toPlainString());
        }
    }

    @Test
    public void testEstimateSqrtRandom() {
        MathContext mathContext = new MathContext(3);
        BigDecimal difference = BigDecimal.ZERO;
        BigDecimal worstDifference = BigDecimal.ZERO;
        boolean myBest = true;
        for (int n = 1; n < 150; n++) {
            System.out.println("Digits limit: " + n);
            for (int i = 0; i < 10000; i++) {
                //System.out.println("\n--------------------------------");
                BigDecimal number = new BigDecimal(BigDecimalGenerator.generateDecimal(n));
                if (number.equals(BigDecimal.ZERO)) {
                    continue;
                }
                //System.out.println("Number:  " + number.toPlainString());

                BigDecimal myEstimation = estimateSqrt(number);
                BigDecimal myError = number.subtract(myEstimation.multiply(myEstimation)).abs();
                myError = myError.divide(number, mathContext);

                BigDecimal fransEstimation = estimateSqrtLelieveld(number);
                BigDecimal fransError = number.subtract(fransEstimation.multiply(fransEstimation)).abs();
                fransError = fransError.divide(number, mathContext);

                difference = difference.add(myError).subtract(fransError);
            }
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                worstDifference = difference;
                myBest = false;
            }
            System.out.println("My error - France's: " + difference.toPlainString());
            difference = BigDecimal.ZERO;
        }
        if (!myBest) {
            System.out.println("Sometimes France do better: " + worstDifference);
        }
    }


    @Test
    public void testEstimateIntSqrt() throws Exception {

        BigInteger number = new BigInteger("0", 2);
        BigInteger sqrt = SquareRootDev.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("0", 2), sqrt);

        number = new BigInteger("1", 2);
        sqrt = SquareRootDev.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("10", 2);
        sqrt = SquareRootDev.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("11", 2);
        sqrt = SquareRootDev.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("100", 2);
        sqrt = SquareRootDev.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("10", 2), sqrt);

        number = new BigInteger("101", 2);
        sqrt = SquareRootDev.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("10", 2), sqrt);

        number = new BigInteger("1001101", 2);
        sqrt = SquareRootDev.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1000", 2), sqrt);

        number = new BigInteger("11010101", 2);
        sqrt = SquareRootDev.estimateIntSqrt(number);
        Assert.assertEquals(new BigInteger("1000", 2), sqrt);

    }

    @Test
    public void testEstimateIntSqrtJScienceImpl() throws Exception {

        BigInteger number = new BigInteger("0", 2);
        BigInteger sqrt = SquareRootDev.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("0", 2), sqrt);

        number = new BigInteger("1", 2);
        sqrt = SquareRootDev.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("0", 2), sqrt);

        number = new BigInteger("10", 2);
        sqrt = SquareRootDev.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("11", 2);
        sqrt = SquareRootDev.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("100", 2);
        sqrt = SquareRootDev.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("101", 2);
        sqrt = SquareRootDev.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1", 2), sqrt);

        number = new BigInteger("1001101", 2);
        sqrt = SquareRootDev.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("100", 2), sqrt);

        number = new BigInteger("1010101", 2);
        sqrt = SquareRootDev.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("101", 2), sqrt);

        number = new BigInteger("11010101", 2);
        sqrt = SquareRootDev.estimateIntSqrtJScienceImpl(number);
        Assert.assertEquals(new BigInteger("1101", 2), sqrt);

    }

    @Test
    public void testAllDec() throws Exception {

        int integerPlaces = 100;
        int fractionalPlaces = 100;
        int sampleSize = 1;

        boolean babylonianValid = true;
        boolean sqrtFransValid = true;
        boolean sqrtCoupledValid = true;
        boolean babylonianByIntValid = true;

        for (int before = 0; before < integerPlaces; before++) {
            for (int after = 0; after < fractionalPlaces; after++) {
                for (int counter = 0; counter < sampleSize; counter++) {

                    String str = BigDecimalGenerator.generateDecimal(before, after);
                    if (!str.equals("")) {

                        BigDecimal exactSqrt = new BigDecimal(str);
                        BigDecimal number = exactSqrt.multiply(exactSqrt);
                        // TODO is that precision correct?
                        MathContext mathContext = new MathContext(exactSqrt.precision() + 1, RoundingMode.HALF_UP);

                        if (babylonianValid) {
                            babylonianValid = testBabylonian(exactSqrt, number, mathContext);
                        }

                        if (sqrtFransValid) {
                            sqrtFransValid = testSqrtFrans(exactSqrt, number, mathContext);
                        }

                        if (sqrtCoupledValid) {
                            sqrtCoupledValid = testCoupledNewton(exactSqrt, number, mathContext);
                        }

                        if (babylonianByIntValid) {
                            babylonianByIntValid = testBabylonianByInteger(exactSqrt, number, mathContext);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testStrinsg() {
        for (String string : strings) {
            BigDecimal exactSqrt = new BigDecimal(string);
            BigDecimal number = exactSqrt.multiply(exactSqrt);
            MathContext mathContext = new MathContext(exactSqrt.precision() + 1, RoundingMode.HALF_UP);
            testBabylonian(exactSqrt, number, mathContext);
            testSqrtFrans(exactSqrt, number, mathContext);
            testCoupledNewton(exactSqrt, number, mathContext);
            testBabylonianByInteger(exactSqrt, number, mathContext);
        }
    }

    @Test
    public void testBabylonianInt() throws Exception {

        BigInteger number;
        BigInteger sqrt;
        BigInteger sqrtSquared;
        BigInteger sqrtPlusSquared;

        number = new BigInteger("-1");
        try {
            sqrt = SquareRootDev.babylonian(number);
            Assert.fail("ArithmeticException should be thrown by SquareRootDev.babylonian(BigInteger) method for negative argument.");
        } catch (ArithmeticException ex) {
        }

        // test numbers 0, ..., 4000
        for (int i = 0; i < 4001; i++) {
            number = new BigInteger(new Integer(i).toString());
            testBabylonianIntegerNumber(number);
        }

        // do random test of big numbers with given number of digits (places)
        for (int places = 10; places < 200; places++) {
            for (int counter = 0; counter < 1000; counter++) {
                number = new BigInteger(BigDecimalGenerator.generateInteger(places));
                testBabylonianIntegerNumber(number);
            }
        }

        number = new BigInteger("64592137654307265023476527674650237456023746501374650234765034765023476503476503476502387465304765645192364528354192364592365492836754192364519236542939");
        sqrt = SquareRootDev.babylonian(number);
        sqrtSquared = sqrt.multiply(sqrt);
        sqrtPlusSquared = sqrtSquared.add(sqrt.shiftLeft(1).add(BigInteger.ONE));
        Assert.assertTrue(number.compareTo(sqrtSquared) >= 0);
        Assert.assertTrue(number.compareTo(sqrtPlusSquared) < 0);
    }

    private static void testBabylonianIntegerNumber(BigInteger number) {

        BigInteger sqrt;
        BigInteger sqrtSquared;
        BigInteger sqrtPlusSquared;
        String msg1 = "SquareRootDev.babylonian(BigInteger number) returns sqrt s.t.: sqrt**2 > number, number = ";
        String msg2 = "SquareRootDev.babylonian(BigInteger number) returns sqrt s.t.: (sqrt + 1)**2 <= number, number = ";


        sqrt = SquareRootDev.babylonian(number);

        sqrtSquared = sqrt.multiply(sqrt);
        Assert.assertTrue(msg1 + number, number.compareTo(sqrtSquared) >= 0);

        sqrtPlusSquared = sqrtSquared.add(sqrt.shiftLeft(1).add(BigInteger.ONE));
        Assert.assertTrue(msg2 + number, number.compareTo(sqrtPlusSquared) < 0);
    }

    @Test
    public void testCoupledNewtonDec() throws Exception {
        String str = "317688.513712171";
        //String str = "0.01";
        BigDecimal exactSqrt = new BigDecimal(str);
        BigDecimal number = exactSqrt.multiply(exactSqrt);
        System.out.println(str);
        System.out.println(BigDecimal.ONE.divide(SquareRootDev.TWO.multiply(exactSqrt), new MathContext(200, RoundingMode.HALF_UP)).toPlainString());
        System.out.println("-----------------------------");

        MathContext mathContext = new MathContext(exactSqrt.precision() + 1, RoundingMode.HALF_UP);

        SquareRootDev.coupledNewtonExploration(number, mathContext);
        //BigDecimal sqrtFrans = SquareRootDev.bigSqrt(number, mathContext);


    }

    @Test
    public void testCoupledNewton() throws Exception {

        String[] sqrts =   {"0", "1", "2", "3", "4", "10", "13", "101", "237", "1346750",
                "23764501287354601287560824765018247650812476510876",
                "24765012384756103247563847560238745682034756083247650183746580134756083476510746513764510473456334576",
                "1.2", "3.74", "11.04506077", "0.00000000342001000000000012", "0.0000000000000000200000202000001",
                "0.00000000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000001",
                "3624591236459342937646.762547126450124765027465012476502476504756014756014765014765014576107456137454",
                "263525467216354736456346592843567829174563219.2634596246350489275642734659812435698254367208734663491"};

        for (int i = 0; i < sqrts.length; i++) {
            testSqureRoot(new BigDecimal(sqrts[i]));
        }

        for (int i = 0; i < 1000; i++) {
            Random generator = new Random();
            int integerPlaces = generator.nextInt(300);
            int fractionalPlaces = generator.nextInt(300);
            String sqrt = BigDecimalGenerator.generateDecimal(integerPlaces, fractionalPlaces);
            if (!sqrt.equals("")) {
                testSqureRoot(new BigDecimal(sqrt));
            }
        }
    }

    private static void testSqureRoot(BigDecimal sqrt) {
        BigDecimal number = sqrt.multiply(sqrt);
        int toleranceScale = number.scale() + 1;
        BigDecimal tolerance = new BigDecimal(new BigInteger("1"), toleranceScale);
        System.out.println(number.toPlainString());
        System.out.println(sqrt.multiply(sqrt).toPlainString());
        System.out.println("-----------------------------");
        Assert.assertTrue(number.subtract(sqrt.multiply(sqrt)).abs().compareTo(tolerance) < 0);
    }

    @Test
    public void testSpeed() {
        List<BigDecimal> decimals = BigDecimalGenerator.generateList();
        MathContext mathContext = new MathContext(20, RoundingMode.HALF_UP);

        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRootDev.bigSqrt(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Frans: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRootDev.babylonian(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRootDev.coupledNewton(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Coupled Newton: " + (endTime - startTime) + " ms");

    }

    @Test
    public void testIntegerSpeed() {

        int size = 100;
        int digits = 2000;

        List<BigInteger> integers = new ArrayList<>(size);
        List<BigDecimal> decimals = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String str = BigDecimalGenerator.generateDecimal(digits, 0);
            integers.add(new BigInteger(str));
            decimals.add(new BigDecimal(str));
        }

        MathContext mathContext = new MathContext(digits, RoundingMode.HALF_UP);

        long startTime;
        long endTime;

        startTime = System.currentTimeMillis();
        for (BigInteger integer : integers) {
            SquareRootDev.babylonian(integer);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian (int): " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRootDev.bigSqrt(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Frans: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRootDev.babylonianByInteger(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian (dec): " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigInteger integer : integers) {
            SquareRootDev.babylonian(integer);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian (int): " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRootDev.bigSqrt(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Frans: " + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        for (BigDecimal decimal : decimals) {
            SquareRootDev.babylonianByInteger(decimal, mathContext);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Babylonian (dec): " + (endTime - startTime) + " ms");

    }

    @Test
    public void testConvert() throws Exception {

        String msg = "Wrong answer for ";

        String str = "0.00123450";
        SquareRootDev.ScaledInteger result = SquareRootDev.convert(str, 3);
        Assert.assertEquals(1235, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 6, result.getScale());

        str = "0.00123450";
        result = SquareRootDev.convert(str, 4);
        Assert.assertEquals(1235, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 6, result.getScale());

        str = "0.0123450";
        result = SquareRootDev.convert(str, 3);
        Assert.assertEquals(123, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 4, result.getScale());

        str = "0.0123450";
        result = SquareRootDev.convert(str, 4);
        Assert.assertEquals(12345, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 6, result.getScale());

        str = "0.01";
        result = SquareRootDev.convert(str, 3);
        Assert.assertEquals(100, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 4, result.getScale());

        str = "41.00001";
        result = SquareRootDev.convert(str, 3);
        Assert.assertEquals(4100, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 2, result.getScale());

        str = "41.0051";
        result = SquareRootDev.convert(str, 3);
        Assert.assertEquals(4101, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 2, result.getScale());

        str = "4.1";
        result = SquareRootDev.convert(str, 4);
        Assert.assertEquals(41000, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 4, result.getScale());

        str = "5324.123044";
        result = SquareRootDev.convert(str, 3);
        Assert.assertEquals(5324, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 0, result.getScale());

        str = "5324.123045";
        result = SquareRootDev.convert(str, 1);
        Assert.assertEquals(53, result.getNumber().intValue());
        Assert.assertEquals(msg + str, -2, result.getScale());

        str = "5354.123046";
        result = SquareRootDev.convert(str, 2);
        Assert.assertEquals(54, result.getNumber().intValue());
        Assert.assertEquals(msg + str, -2, result.getScale());

        str = "5354.123046";
        result = SquareRootDev.convert(str, 1);
        Assert.assertEquals(54, result.getNumber().intValue());
        Assert.assertEquals(msg + str, -2, result.getScale());

        str = "53254.123045";
        result = SquareRootDev.convert(str, 1);
        Assert.assertEquals(5, result.getNumber().intValue());
        Assert.assertEquals(msg + str, -4, result.getScale());


        BigDecimal number = new BigDecimal("1230000");
        number.setScale(-3);
        result = SquareRootDev.convert(number, 7);
        Assert.assertEquals(1230000, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 0, result.getScale());
        result = SquareRootDev.convert(number, 3);
        Assert.assertEquals(123, result.getNumber().intValue());
        Assert.assertEquals(msg + str, -4, result.getScale());
        result = SquareRootDev.convert(number, 4);
        Assert.assertEquals(12300, result.getNumber().intValue());
        Assert.assertEquals(msg + str, -2, result.getScale());
        result = SquareRootDev.convert(number, 8);
        Assert.assertEquals(123000000, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 2, result.getScale());
        result = SquareRootDev.convert(number, 9);
        Assert.assertEquals(123000000, result.getNumber().intValue());
        Assert.assertEquals(msg + str, 2, result.getScale());

    }

    private boolean testBabylonian(BigDecimal exactSqrt, BigDecimal number, MathContext sqrtContext) {
        BigDecimal sqrt = SquareRootDev.babylonian(number, sqrtContext);
        BigDecimal rounded = exactSqrt.round(sqrtContext);
        boolean isValid = (rounded.compareTo(sqrt) == 0);
        if (!isValid) {
            System.out.println("SquareRootDev.babylonian error for: " + exactSqrt.toPlainString());
            System.out.println("Precision: " + sqrtContext.getPrecision());
            System.out.println("Rounding mode: " + sqrtContext.getRoundingMode());
        }
        return isValid;
    }

    private boolean testSqrtFrans(BigDecimal exactSqrt, BigDecimal number, MathContext sqrtContext) {
        BigDecimal sqrt = SquareRootDev.bigSqrt(number, sqrtContext);
        BigDecimal rounded = exactSqrt.round(sqrtContext);
        boolean isValid = (rounded.compareTo(sqrt) == 0);
        if (!isValid) {
            System.out.println("SquareRootDev.bigSqrt error for: " + exactSqrt.toPlainString());
            System.out.println("Precision: " + sqrtContext.getPrecision());
            System.out.println("Rounding mode: " + sqrtContext.getRoundingMode());
        }
        return isValid;
    }

    private boolean testCoupledNewton(BigDecimal exactSqrt, BigDecimal number, MathContext sqrtContext) {
        BigDecimal sqrt = SquareRootDev.coupledNewton(number, sqrtContext);
        BigDecimal rounded = exactSqrt.round(sqrtContext);
        boolean isValid = (rounded.compareTo(sqrt) == 0);
        if (!isValid) {
            System.out.println("SquareRootDev.coupledNewton error for: " + exactSqrt.toPlainString());
            System.out.println("Precision: " + sqrtContext.getPrecision());
            System.out.println("Rounding mode: " + sqrtContext.getRoundingMode());
        }
        return isValid;
    }

    private boolean testBabylonianByInteger(BigDecimal exactSqrt, BigDecimal number, MathContext sqrtContext) {
        BigDecimal sqrt = SquareRootDev.babylonianByInteger(number, sqrtContext);
        BigDecimal rounded = exactSqrt.round(sqrtContext);
        boolean isValid = (rounded.compareTo(sqrt) == 0);
        if (!isValid) {
            System.out.println("SquareRootDev.babylonianByInteger error for: " + exactSqrt.toPlainString());
            System.out.println("Precision: " + sqrtContext.getPrecision());
            System.out.println("Rounding mode: " + sqrtContext.getRoundingMode());
        }
        return isValid;
    }

    @Test
    public void fransError() {
        String str = "0.352818528921979802147666515426902895727765101266038472739273675725801740461899203391467404999652538893639944052164993861983322930689421050680216977023059231670566602635420201308103469059177274411042449967235640170639404861525798685782654348576008053906678123883330032297238500985193573602497626771580945594770502324168762166567267289269665176402913948019856635315861428993880073282624381118485716289019571647659739495016074896218885978289930340857274515862259731833170589692399895634244147482703269890913247408478923447178902017779845443742308844558419999046111467812933248023432427998259994182648578288775189490102071574925542172618705808377981407075661662506742408661232720032172324989308087757531861080580705851938971046656424285025030048776692043487994681169121806548569325273977674268512834848319197541770346893931298171263359209287656256258646644947830231869419567842967424731270811652666974200603722897904972781881580215186731799243387581020765695927222985513092811072096813507351486163616474354491678516423019462856092324589467805417576434369118271125610599862858091034066115393896376402586115322883185629500789989825539099146963464597667690086645833677773341477425089421202029845828438186267290619982627882863531585505515611951359679835295645460495124946561810293085634756353824263832486284294130150330118760419800451287490407783325022093281030504666007839427958453674663221723404057631681719148427415412519993998928227188905950986505087130321733361332156307945821264095564986339643413952656671077302618008809419170629325044973291573447163149540989060334863465155227701549160360633076458829101325288181727254148455952801479839414624838237422509188727938777531618602609770842427633605357541405468766390741255444162263154213109493016536875507122768177992567211419631697956177013676147279765005362827247748175113476912061656596560057107219180031161086243871513570693257886733246854019430780326947936432096164343749503593404634630874853900858530925398333908462443249715617740821018862850469001766145798985952338158907879181848149273610535411146431164860190045987407079776737665145855618269772150097545656533084155333408671462357567952863327464733354130776616979840611935007402670571489089668548000566788250160846791012993775567439582182725205696062583108329707566149812378805220075179590531688600283608991116551549027626948019331454835003314143801424170814628183200055052199722542207640173290280989264977286415610857154441019808138140819856280410105031134448183196991569236564590128053475070955652666628369465344771805924916499026673246861918192823814334395024295138823232784709065496229159508579419100068387896656615250289291758465598666278370085239497533286365488975303055116815329102108942419789159408743304837147769168826679563309806682084876882871097306440805958691670681692306549364491345283413772350464609125873897690388407474850326909999101415928102266360806164931776847358063319741306986987628745641280473524000212369792965384091797156403233777795567784543089119411659393168440334248938741623001128517522693879403929699173451781412798671665283558729628243926890969910443705973489192155576353499381779194610671587110478486168711282156757950565546363036019684717556713624143788093810345688751549193665721896867887651320967000222128167164946865556889602661431879227060777814881492519547779448310707438750917861990561928144098861424157634058497094462991766917173509132343518475084202673630176061598854141914185610767706802039701642072360625842378688027735898240182190514393189209471611768527100950310316851880853478199424580668628085693182998731100236819825441203790704941193496754099933586278887732102406442366897274992111227286780538771607254703391727435908633086669314433562019482301856480278389899972125503889024035852303562592479624352423337002906871575935923566494087298753092343486998023539344694510533797805958900582731852193990439519387549384829431113980579322708657363957164395621754828226758348067425530114629280261288667931574298350041682965467047588359489270073143306999546737192711175822029705263447027865758551542802171488905955004349464501480093234324812778987292165459441941026474022142289145222953357220372418411212413061286574450794826437795190069128780886322873705075391534786287589843850190532006721823083493988784719966839685662262264057458858102263578441838880872379410128677308321014897665822450598742323482617706556668314972948257303337118691806018319646689269303327895611192483290610412447084434138915732180208661257866451990865563784573699574180113438083551175839547069343207045045995100912147324966281922502715105823762488576030121131547913526483123729519773892343318565326104868289395276455971388735259382226159413912984029551781738507760341551002029698244452208252501876382885200201519676863857276544629621043623553022169433277290829018245988763416544714675486727259277158207185683059120469903788050023125005340124434545488528442744313808998213896416775571508334831773577301265300131926124116720552814768332474881127016239632373827247236037212368475176197284063580380941316779730166327775936730580436889438218610438044839337499052077613183040131513844261362138421340482537949355008184657331131802878928188016227607049663793923496957161690184263051610305144160357410316194354127288952746801158072255627528356254285419729629914150135164293325498786148924752199146507320087447951639563162872093511891508859874602833528357633853230657750414976922360211994756093221208726220406868776436423802282853111630343720137345935295997001814433021729906260871559252222785750795910525236903608970515342971450150471140143424730302419832493045288822449089904121958127502411554916890000523296881346660246083743133530717406880789993705668249624474982096681744820733458578119329965309671669003956057605867177163829210966938191726511853645119095575338547037668182994797666943720389432824576727544324133629219070818056301690488891414444637139445286226789026200554535161974614349396819623126119487492383584029405209572120869911038247087356343641894799034464020392552038747499914221074709047988903494270079315963932888035593068602395612879290719088991015317352105164692480211676756550307172096101637696730714405029471608453893636023178565563010040550935381304155988922549067016051209896944367217070301142516901257757847663657027699473130521129513917273661361656490890291200300878354926042615514456147252662943337364841398596952462369372282916304134920158141647180675038504459880237319";
        BigDecimal exactSqrt = new BigDecimal(str);
        BigDecimal number = exactSqrt.multiply(exactSqrt);
        MathContext mathContext = new MathContext(exactSqrt.precision() + 1, RoundingMode.HALF_UP);
        BigDecimal sqrt = SquareRootDev.bigSqrt(number, mathContext);
        BigDecimal rounded = exactSqrt.round(mathContext);
        BigDecimal difference = rounded.subtract(sqrt);
        System.out.println("Difference = " + difference.toPlainString());

        // 4 * 10^74 + 1
        number = new BigDecimal(new BigInteger("4"), -74).add(BigDecimal.ONE);
        //number = new BigDecimal("400000000000000000000000000000000000000000000000000000000000000000000000001");
        mathContext = new MathContext(1, RoundingMode.UP);
        System.out.println(SquareRootDev.bigSqrt(number, mathContext));
    }
}