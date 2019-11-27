package com.example.acoustic_motion_tracking_receiver;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_conv() {
        double[] A = {1,2,3};
        double[] B = {4,5,6,7};
        double[] C = SignalProcessingUtil.conv(A, B);
        double[] std_C = {4, 13, 28, 34, 32, 21};
        assertArrayEquals(std_C, C, 1e-9);
    }

    @Test
    public void test_xcorr() {
        double[] A = {1,2,3};
        double[] B = {4,5,6,7};
        double[] C = SignalProcessingUtil.xcorr(A, B);
        double[] std_C = {7, 20, 38, 32, 23, 12, 0};
        assertArrayEquals(std_C, C, 1e-9);
    }

    @Test
    public void test_chirp() {
        double fs = 44100;
        double T = 0.01;
        double f0 = 18000;
        double f1 = 20500;
        double[] signal = SignalProcessingUtil.chirp_linear(fs, f0, T, f1);

        double[] std_signal = {1, -0.838308347067902, 0.406259931387485, 0.156010849500612, -0.667503590056651, 0.964684691848846, -0.953536443868912, 0.638837850419380, -0.121714620407356, -0.433294209159698, 0.850796381846521, -0.999883734472946, 0.834821452128047, -0.408561290826158, -0.144736062657441, 0.652259861696499, -0.956590740371322, 0.964309276372034, -0.674235822114113, 0.176908423940033, 0.374106291308261, -0.809853625042314, 0.997740793395361, -0.881568118241518, 0.497858075993947, 0.0361038048846214, -0.558472200353761, 0.912013408346009, -0.991276153231053, 0.773663021717920, -0.325368826126680, -0.219196668395922, 0.697982367837211, -0.969541923150383, 0.954586669705964, -0.658713282223910, 0.169924400513996, 0.367941009008269, -0.797736836150888, 0.994870272264749, -0.903130302233993, 0.550271286690481, -0.0391708368450300, -0.482511385586958, 0.865131531279666, -0.999876735221597, 0.849384198850042, -0.457647535980372, -0.0632722330633254, 0.565606875486555, -0.907886943135621, 0.994632271286361, -0.802638972113076, 0.386729768995156, 0.136307546062187, -0.620783370773949, 0.932741085481703, -0.986819627724268, 0.769253683278584, -0.340919392628385, -0.180087282692084, 0.651255244871030, -0.944671571881260, 0.981559156991066, -0.753069340367377, 0.321945158286668, 0.195081410131542, -0.659126417726374, 0.946484946593346, -0.981419941091727, 0.755773134936948, -0.330420095164045, -0.181517157862967, 0.644981433088053, -0.938638465572809, 0.986464383240405, -0.777090554460743, 0.366077726705266, 0.139187497811898, -0.607782173592394, 0.919214484246230, -0.994249621011225, 0.814752668011208, -0.427666208341121, -0.0676243841664801, 0.545054203837557, -0.884034237787174, 0.999783898966882, -0.864247092189482, 0.512509557776731, -0.0333593292510639, -0.453390925473230, 0.826966061588697, -0.995510785115285, 0.918423920652656, -0.615781123734625, 0.162756512643861, 0.329337381219830, -0.740544013109012, 0.971469354047800, -0.967102003347885, 0.729592437091037, -0.317108183937211, -0.170720285167975, 0.617053099661052, -0.915848615694055, 0.996909756334670, -0.842092507871515, 0.489006490228881, -0.0215554768368200, -0.450234931740966, 0.816201853432149, -0.991688484209153, 0.936899550505395, -0.665600967119954, 0.241345053713028, 0.237690795695921, -0.661576824767986, 0.933854839203346, -0.993329937907914, 0.827546897100867, -0.475051745681851, 0.0161287953627153, 0.445700078311069, -0.807106161493896, 0.987997336479070, -0.949055747010677, 0.699980890569170, -0.296784349846693, -0.171070439164211, 0.600677195662776, -0.898338232210046, 0.999872127685763, -0.884207840775157, 0.577443780890806, -0.146669804348036, -0.314931908550278, 0.708353577549219, -0.949967964691641, 0.989141632074253, -0.818584137912510, 0.475378648631243, -0.0326489525656742, -0.416213669020998, 0.777324341220853, -0.975880347229158, 0.971473185021257, -0.766044443118976, 0.402980475444126, 0.0423020569656914, -0.478190137882558, 0.815772306134061, -0.986895327216447, 0.957731999143118, -0.735201709460225, 0.365085380253684, 0.0776293137169523, -0.504093318478115, 0.829464593712401, -0.989701654227629, 0.953978288377067, -0.730354243658517, 0.363716464692683, 0.0734574571393046, -0.495572811503913, 0.820712534504463, -0.986453785909359, 0.961671156601652, -0.752133298136719, 0.398940530362563, 0.0297588447971265, -0.452065808772430, 0.788021693231331, -0.974681089246812, 0.977740703904635, -0.797610188300709, 0.468899522326443, -0.0535482061599171, -0.371003193268297, 0.726314124691164, -0.947393540354884, 0.994446839111571, -0.859826003222206, 0.569012352323556, -0.175572750718989, -0.248877950116281, 0.627799551097239, -0.893516614017892, 0.999196361437204, -0.926916757346058, 0.690471621218168, -0.332523280413349, -0.0832808829098673, 0.483741861381510, -0.799020805846161, 0.974749670697227, -0.981255310627380, 0.818361392785056, -0.515031843996177, 0.124023130971898, 0.287437648839411, -0.649279442854305, 0.900498748113432, -0.999337211086754, 0.930048110059088, -0.705188078347264, 0.363144596830850, 0.0385897424539170, -0.433192296288823, 0.755656764993202, -0.953448801932859, 0.994935428358635, -0.874259609662239, 0.611942792300090, -0.251208398422932, -0.149289847675899, 0.525090002488096, -0.816304504503350, 0.977092128188549, -0.982734995620507, 0.833252919069235, -0.553060248780848, 0.186804336418616, 0.207881669337243, -0.569517024792044, 0.842345070392957, -0.984851200649146, 0.975940260193406, -0.817878356420863, 0.535621102338924, -0.172709508420930, -0.215570161924603, 0.570671650745587, -0.839609379100433, 0.982788751426585, -0.979702445735058, 0.831695589421260, -0.561456745994804, 0.209382502886489, 0.172582219138095, -0.528673441933145, 0.807449366425395, -0.969156956677395, 0.991280410878360, -0.871516805980200, 0.627812124672084, -0.295519165100859, -0.0778547814231318, 0.439514878456564, -0.738859101866211, 0.934511880847569, -0.999941781085629, 0.926916757346027, -0.726369639681510, 0.426614579232488, -0.0692198549684845, -0.296846052873490, 0.621960443409478, -0.862551922608876, 0.986864029555088, -0.979026676630932, 0.840931837505997, -0.591715855508220, 0.264973144179530, 0.0958783131663061, -0.443428344200634, 0.732513263393640, -0.926041328145464, 0.999660983429343, -0.944698062340241, 0.769026565722101, -0.495811312726102, 0.160333397418847, 0.194653620744698, -0.524416206909425, 0.787872495096953, -0.952656397959578, 0.998998350154745, -0.921971160254762, 0.731864125913894, -0.452685281248256, 0.119020526579116, 0.228328223275200, -0.547368025126860, 0.799981399358048, -0.956425816055456, 0.998743195215058, -0.922701426330173, 0.738074857214879, -0.467272198708554, 0.142513974901255, 0.198074892208892, -0.514976456898612, 0.771858545035492, -0.939692620785962, 0.999950973285276, -0.946542281051742, 0.786298133313583, -0.537992543437689, 0.230042112755882, 0.102822182489582, -0.423514769866252, 0.696731970464266, -0.892804650772013, 0.990851705432735, -0.980908188085022, 0.864823499112792, -0.655864554845130, 0.377100480891715, -0.0587737467651144, -0.265035448919069, 0.560079136958579, -0.795549681697328, 0.947248673934421, -0.999997025122609, 0.949055747010672, -0.800436722633310, 0.570101114279636, -0.282157353291093, -0.0337306558340682, 0.345440852100289, -0.621669469969989, 0.835052595494432, -0.964824970119264, 0.998764157434553, -0.934247140043192, 0.778339557527906, -0.546935346456695, 0.263056734655185, 0.0454974097294547, -0.348909962756086, 0.618234592307867, -0.828135902826087, 0.959224442163111, -0.999777130604800, 0.946698495850072, -0.805654066206006, 0.590386781278304, -0.321302749938885, 0.0234772825023634, 0.275719652980968, -0.549151277357419, 0.772361555063273, -0.925723969268926, 0.996107265300583, -0.977923355389810, 0.873482137374715, -0.692641902077525, 0.451806429933878, -0.172375367907432, -0.121201528182136, 0.403586532162561, -0.650740242397790, 0.841944368058588, -0.961493778330028, 0.999924442211814, -0.954687682187709, 0.830230750715093, -0.637494684090171, 0.392888372362764, -0.116838950535639, -0.167950129331935, 0.438382761850263, -0.672838833722944, 0.852877881440397, -0.964646386560559, 0.999885696687707, -0.956472968487473, 0.838466836780317, -0.655669427318666, 0.422753679307368, -0.158036922689926, -0.117993973472342, 0.384285330736927, -0.620821362741720, 0.810109407896117, -0.938437734218717, 0.996819004449357, -0.981559156991048, 0.894421480543953, -0.742386641644630, 0.537039025359548, -0.293635858131143, 0.0299364566067111, 0.235116677740711, -0.482766045055064, 0.695757972886065, -0.859520677929258, 0.963123547033052, -0.999957907083632, 0.968101212307150, -0.870349928885789, 0.713929734557856, -0.509913364109170, 0.272395110864478, -0.0174855165211463, -0.237800629591391, 0.476685603770767, -0.683723086621352, 0.845775335341882, -0.952813439438085, 0.998494788579858, -0.980488113356386, 0.900533868108335, -0.764245123442276, 0.580670406525666, -0.361654088485931, 0.121041180213531, 0.126218799522635, -0.365010185568222, 0.580972848031450, -0.761351708591463, 0.895725262953667, -0.976573592815246, 0.999657610343414, -0.964193701387847, 0.872820711978436, -0.731368574144259, 0.548449081662907, -0.334898803827425, 0.103111404057587, 0.133698594795770, -0.362256425652501, 0.569968391947900, -0.745603144178686, 0.879877273877312, -0.965916418644641, 0.999571175136325, -0.979575964187745, 0.907548052762037, -0.787832701114990, 0.627208408345269, -0.434473083368015, 0.219937366413899, 0.00514493881558637, -0.229177394945424, 0.440820299691066, -0.629558256116243, 0.786208300188794, -0.903345200949696, 0.975625782362897, -1};

        assertArrayEquals(std_signal, signal, 1e-8);
    }

    @Test
    public void test_FMCW() {
        ArrayList<String> data = new ArrayList<>();
        try {
            File file = new File("D:\\THU\\大四上\\网络系统(2)\\声音定位基础\\test_signal_data.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String strLine = null;
            while(null != (strLine = bufferedReader.readLine())){
                data.add(strLine);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        double[] data_array = new double[data.size()];
        int idx = 0;
        for (String d: data)
            data_array[idx++] = Double.parseDouble(d);
        double[] delta_distance = FMCW.get_distance(data_array);
//        for (double c: delta_distance)
//            System.out.println(c);

        double[] std_delta_distance = {0.00544000000000000, 0.00544000000000000, 0.00544000000000000, 0.00544000000000000, 0.00544000000000000, 0.00544000000000000, 0.141440000000000, 0.174080000000000, 0.141440000000000, 0.0652800000000000, 0.0435200000000000, 0.157760000000000, 0.184960000000000, 0.195840000000000, 0.00544000000000000, 0.184960000000000, 0.141440000000000, 0.0761600000000000, 0.157760000000000, 0.146880000000000, 0.163200000000000, 0.184960000000000, 0.174080000000000, 0.168640000000000, 0.179520000000000, 0.179520000000000, 0.179520000000000, 0.179520000000000, 0.217600000000000, 0.212160000000000, 0.206720000000000, 0.201280000000000, 0.195840000000000, 0.195840000000000, 0.195840000000000, 0.195840000000000, 0.212160000000000, 0.239360000000000, 0.255680000000000, 0.212160000000000, 0.212160000000000, 0.277440000000000, 0.212160000000000, 0.272000000000000, 0.277440000000000, 0.293760000000000, 0.282880000000000, 0.320960000000000, 0.310080000000000, 0.320960000000000, 0.353600000000000, 0.380800000000000, 0.391680000000000, 0.402560000000000, 0.413440000000000, 0.408000000000000, 0.424320000000000, 0.456960000000000, 0.462400000000000, 0.484160000000000, 0.516800000000000, 0.538560000000000, 0.527680000000000, 0.505920000000000, 0.522240000000000, 0.527680000000000, 0.544000000000000, 0.511360000000000, 0.571200000000000, 0.565760000000000, 0.527680000000000, 0.500480000000000, 0.614720000000000, 0.641920000000000, 0.652800000000000, 0.647360000000000, 0.592960000000000, 0.652800000000000, 0.587520000000000, 0.571200000000000, 0.554880000000000, 0.603840000000000, 0.641920000000000, 0.647360000000000, 0.767040000000000, 0.870400000000000, 0.859520000000000, 0.718080000000000};

        assertArrayEquals(std_delta_distance, delta_distance, 1e-2);
    }
}